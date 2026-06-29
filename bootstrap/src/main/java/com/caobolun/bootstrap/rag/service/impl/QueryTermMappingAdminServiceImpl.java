package com.caobolun.bootstrap.rag.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caobolun.bootstrap.core.rewrite.QueryTermMappingCacheManager;
import com.caobolun.bootstrap.rag.dto.request.QueryTermMappingCreateRequest;
import com.caobolun.bootstrap.rag.dto.request.QueryTermMappingPageRequest;
import com.caobolun.bootstrap.rag.dto.request.QueryTermMappingUpdateRequest;
import com.caobolun.bootstrap.rag.dto.vo.QueryTermMappingVO;
import com.caobolun.bootstrap.rag.mapper.QueryTermMappingMapper;
import com.caobolun.bootstrap.rag.service.QueryTermMappingAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryTermMappingAdminServiceImpl implements QueryTermMappingAdminService {

    private final QueryTermMappingMapper queryTermMappingMapper;
    private final QueryTermMappingCacheManager queryTermMappingCacheManager;

    @Override
    public String create(QueryTermMappingCreateRequest requestParam) {
        return "";
    }

    @Override
    public void update(String id, QueryTermMappingUpdateRequest requestParam) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public QueryTermMappingVO queryById(String id) {
        return null;
    }

    @Override
    public IPage<QueryTermMappingVO> pageQuery(QueryTermMappingPageRequest requestParam) {
        return null;
    }
}
