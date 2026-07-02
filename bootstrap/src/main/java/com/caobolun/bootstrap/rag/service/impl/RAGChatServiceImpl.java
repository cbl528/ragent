package com.caobolun.bootstrap.rag.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.caobolun.bootstrap.rag.handler.StreamCallbackFactory;
import com.caobolun.bootstrap.rag.handler.StreamTaskManager;
import com.caobolun.bootstrap.rag.pipeline.StreamChatContext;
import com.caobolun.bootstrap.rag.pipeline.StreamChatPipeline;
import com.caobolun.bootstrap.rag.ratelimit.ChatQueueLimiter;
import com.caobolun.bootstrap.rag.service.RAGChatService;
import com.caobolun.bootstrap.rag.trace.StreamChatTraceRunner;
import com.caobolun.framework.context.UserContext;
import com.caobolun.infraai.chat.StreamCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class RAGChatServiceImpl implements RAGChatService {

    private final StreamChatPipeline streamChatPipeline; // 流式聊天管道
    private final ChatQueueLimiter chatQueueLimiter; // 聊天队列限流器
    private final StreamCallbackFactory callbackFactory; // 流式回调处理器工厂
    private final StreamChatTraceRunner chatTraceRunner; // 聊天跟踪运行器
    private final StreamTaskManager taskManager;

    @Override
    public void streamChat(String question, String conversationId, Boolean deepThinking, SseEmitter emitter) {
        // 获取实际的会话ID，如果为空就通过雪花算法生成一个
        String actualConversationId = StrUtil.isBlank(conversationId) ? IdUtil.getSnowflakeNextIdStr() : conversationId;
        // 生成任务ID
        String taskId = IdUtil.getSnowflakeNextIdStr();
        // 创建流式回调处理器
        StreamCallback callback = callbackFactory.createChatEventHandler(emitter, actualConversationId, taskId);
        chatQueueLimiter.enqueue(question, actualConversationId, emitter, // 排队限流
                () -> chatTraceRunner.run(question, actualConversationId, taskId, callback, // 全链路跟踪
                        traceAware -> { // 真正的业务逻辑
                    StreamChatContext context = StreamChatContext.builder()
                            .question(question)
                            .conversationId(actualConversationId)
                            .deepThinking(Boolean.TRUE.equals(deepThinking))
                            .userId(UserContext.getUserId())
                            .build();
                    streamChatPipeline.execute(context);
                }));
    }

    @Override
    public void stopTask(String taskId) {
        taskManager.cancel(taskId);
    }
}
