package com.caobolun.bootstrap.rag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caobolun.bootstrap.rag.dto.request.QueryTermMappingCreateRequest;
import com.caobolun.bootstrap.rag.dto.request.QueryTermMappingPageRequest;
import com.caobolun.bootstrap.rag.dto.request.QueryTermMappingUpdateRequest;
import com.caobolun.bootstrap.rag.dto.vo.QueryTermMappingVO;

public interface QueryTermMappingAdminService {

    /**
     * 创建映射规则
     */
    String create(QueryTermMappingCreateRequest requestParam);

    /**
     * 更新映射规则
     */
    void update(String id, QueryTermMappingUpdateRequest requestParam);

    /**
     * 删除映射规则
     */
    void delete(String id);

    /**
     * 查询映射规则详情
     */
    QueryTermMappingVO queryById(String id);

    /**
     * 分页查询映射规则
     */
    IPage<QueryTermMappingVO> pageQuery(QueryTermMappingPageRequest requestParam);
}
