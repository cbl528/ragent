package com.caobolun.bootstrap.rag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caobolun.bootstrap.rag.dto.request.RagTraceRunPageRequest;
import com.caobolun.bootstrap.rag.dto.vo.RagTraceDetailVO;
import com.caobolun.bootstrap.rag.dto.vo.RagTraceNodeVO;
import com.caobolun.bootstrap.rag.dto.vo.RagTraceRunVO;

import java.util.List;

/**
 * RAG Trace 查询服务
 */
public interface RagTraceQueryService {

    IPage<RagTraceRunVO> pageRuns(RagTraceRunPageRequest request);

    RagTraceDetailVO detail(String traceId);

    List<RagTraceNodeVO> listNodes(String traceId);
}
