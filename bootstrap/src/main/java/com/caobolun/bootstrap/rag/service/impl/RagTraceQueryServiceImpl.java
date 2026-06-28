package com.caobolun.bootstrap.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caobolun.bootstrap.rag.dto.request.RagTraceRunPageRequest;
import com.caobolun.bootstrap.rag.dto.vo.RagTraceDetailVO;
import com.caobolun.bootstrap.rag.dto.vo.RagTraceNodeVO;
import com.caobolun.bootstrap.rag.dto.vo.RagTraceRunVO;
import com.caobolun.bootstrap.rag.entity.RagTraceNodeDO;
import com.caobolun.bootstrap.rag.entity.RagTraceRunDO;
import com.caobolun.bootstrap.rag.mapper.RagTraceNodeMapper;
import com.caobolun.bootstrap.rag.mapper.RagTraceRunMapper;
import com.caobolun.bootstrap.rag.service.RagTraceQueryService;
import com.caobolun.bootstrap.user.entity.UserDO;
import com.caobolun.bootstrap.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagTraceQueryServiceImpl implements RagTraceQueryService {

    private final RagTraceRunMapper runMapper;
    private final RagTraceNodeMapper nodeMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<RagTraceRunVO> pageRuns(RagTraceRunPageRequest request) {
        LambdaQueryWrapper<RagTraceRunDO> wrapper = Wrappers.lambdaQuery(RagTraceRunDO.class)
                .orderByDesc(RagTraceRunDO::getStartTime);
        if(StrUtil.isNotBlank(request.getTraceId())){
            wrapper.eq(RagTraceRunDO::getTraceId, request.getTraceId());
        }
        if(StrUtil.isNotBlank(request.getConversationId())){
            wrapper.eq(RagTraceRunDO::getConversationId, request.getConversationId());
        }
        if (StrUtil.isNotBlank(request.getTaskId())) {
            wrapper.eq(RagTraceRunDO::getTaskId, request.getTaskId());
        }
        if (StrUtil.isNotBlank(request.getStatus())) {
            wrapper.eq(RagTraceRunDO::getStatus, request.getStatus());
        }

        IPage<RagTraceRunDO> pageResult = runMapper.selectPage(request, wrapper);
        Map<String, String> usernameMap = loadUsernameMap(pageResult.getRecords());
        Map<String, Long> ttftMap = loadTtftMap(pageResult.getRecords());
        return pageResult.convert(run -> toRunVO(run, usernameMap, ttftMap));
    }


    /**
     * 将RagTraceRunDO转换为RagTraceRunVO
     * @param run RAG追踪运行对象
     * @param usernameMap 用户名映射
     * @param ttftMap TTFt映射
     * @return RAG追踪运行VO对象
     */
    private RagTraceRunVO toRunVO(RagTraceRunDO run, Map<String, String> usernameMap, Map<String, Long> ttftMap) {
        String username = resolveUsername(run.getUserId(), usernameMap);
        String question = parseQuestion(run.getExtraData());
        return RagTraceRunVO.builder()
                .traceId(run.getTraceId())
                .traceName(run.getTraceName())
                .entryMethod(run.getEntryMethod())
                .conversationId(run.getConversationId())
                .taskId(run.getTaskId())
                .userId(run.getUserId())
                .username(username)
                .status(run.getStatus())
                .errorMessage(run.getErrorMessage())
                .durationMs(run.getDurationMs())
                .ttftMs(ttftMap.get(run.getTraceId()))
                .question(question)
                .startTime(run.getStartTime())
                .endTime(run.getEndTime())
                .build();
    }

    /**
     * 解析问题
     * @param extraData 额外数据
     * @return 问题
     */
    private String parseQuestion(String extraData) {
        if (StrUtil.isBlank(extraData)) {
            return null;
        }
        try {
            JSONObject json = JSONUtil.parseObj(extraData);
            return json.getStr("question");
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 解析用户名
     * @param userId 用户ID
     * @param usernameMap 用户名映射
     * @return 用户名
     */
    private String resolveUsername(String userId, Map<String, String> usernameMap) {
        if (StrUtil.isBlank(userId) || usernameMap == null || usernameMap.isEmpty()) {
            return null;
        }
        return usernameMap.get(userId);
    }

    /**
     * 加载用户名映射
     * @param runs RAG追踪运行对象列表
     * @return 用户名映射
     */
    private Map<String, String> loadUsernameMap(List<RagTraceRunDO> runs) {
        if(runs == null || runs.isEmpty()){
            return Collections.emptyMap();
        }

        Set<String> userIds = runs.stream()
                .map(RagTraceRunDO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if(userIds.isEmpty()){
            return Collections.emptyMap();
        }
        List<UserDO> userDOS = userMapper.selectList(Wrappers.lambdaQuery(UserDO.class)
                .in(UserDO::getId, userIds)
                .select(UserDO::getId, UserDO::getUsername));
        if(userDOS == null || userDOS.isEmpty()){
            return Collections.emptyMap();
        }
        return userDOS.stream().collect(Collectors.toMap(
                user -> String.valueOf(user.getId()), // 将用户ID转换为字符串，作为Map的key
                UserDO::getUsername, // 将用户名作为Map的value
                (left, right) -> left // 处理key冲突，保留左值，丢弃右值
        ));
    }

    /**
     * 加载TTFt映射
     * @param runs RAG追踪运行对象列表
     * @return TTFt映射
     */
    private Map<String, Long> loadTtftMap(List<RagTraceRunDO> runs) {
        if(runs == null || runs.isEmpty()){
            return Collections.emptyMap();
        }

        List<String> traceIds = runs.stream()
                .map(RagTraceRunDO::getTraceId)
                .filter(Objects::nonNull)
                .toList();
        if(traceIds.isEmpty()){
            return Collections.emptyMap();
        }

        List<RagTraceNodeDO> ttftNodes = nodeMapper.selectList(
                Wrappers.lambdaQuery(RagTraceNodeDO.class)
                        .in(RagTraceNodeDO::getTraceId, traceIds)
                        .eq(RagTraceNodeDO::getNodeType, "USER_TTFT")
                        .select(RagTraceNodeDO::getTraceId, RagTraceNodeDO::getDurationMs)
        );

        if(ttftNodes == null || ttftNodes.isEmpty()){
            return Collections.emptyMap();
        }

        return ttftNodes.stream()
                .filter(node -> node.getTraceId() != null && node.getDurationMs() != null)
                .collect(Collectors.toMap(
                        RagTraceNodeDO::getTraceId,
                        RagTraceNodeDO::getDurationMs,
                        (left, right) -> left));
    }


    @Override
    public RagTraceDetailVO detail(String traceId) {
        RagTraceRunDO run = runMapper.selectOne(Wrappers.lambdaQuery(RagTraceRunDO.class)
                .eq(RagTraceRunDO::getTraceId, traceId)
                .last("limit 1"));
        if(run == null){
            return null;
        }
        Map<String, String> usernameMap = loadUsernameMap(List.of(run));
        Map<String, Long> ttftMap = loadTtftMap(List.of(run));
        return RagTraceDetailVO.builder()
                .run(toRunVO(run, usernameMap, ttftMap))
                .nodes(listNodes(traceId))
                .build();
    }

    @Override
    public List<RagTraceNodeVO> listNodes(String traceId) {
        List<RagTraceNodeDO> nodes = nodeMapper.selectList(Wrappers.lambdaQuery(RagTraceNodeDO.class)
                .eq(RagTraceNodeDO::getTraceId, traceId)
                .orderByAsc(RagTraceNodeDO::getStartTime)
                .orderByAsc(RagTraceNodeDO::getId));
        return nodes.stream().map(this::toNodeVO).toList();
    }

    private RagTraceNodeVO toNodeVO(RagTraceNodeDO node) {
        return RagTraceNodeVO.builder()
                .traceId(node.getTraceId())
                .nodeId(node.getNodeId())
                .parentNodeId(node.getParentNodeId())
                .depth(node.getDepth())
                .nodeType(node.getNodeType())
                .nodeName(node.getNodeName())
                .className(node.getClassName())
                .methodName(node.getMethodName())
                .status(node.getStatus())
                .errorMessage(node.getErrorMessage())
                .durationMs(node.getDurationMs())
                .startTime(node.getStartTime())
                .endTime(node.getEndTime())
                .build();
    }
}
