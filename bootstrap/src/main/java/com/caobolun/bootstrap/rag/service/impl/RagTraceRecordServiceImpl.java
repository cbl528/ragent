package com.caobolun.bootstrap.rag.service.impl;

import com.caobolun.bootstrap.rag.entity.RagTraceNodeDO;
import com.caobolun.bootstrap.rag.entity.RagTraceRunDO;
import com.caobolun.bootstrap.rag.service.RagTraceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RagTraceRecordServiceImpl implements RagTraceRecordService {
    @Override
    public void startRun(RagTraceRunDO run) {

    }

    @Override
    public void finishRun(String traceId, String status, String errorMessage, Date endTime, long durationMs) {

    }

    @Override
    public void startNode(RagTraceNodeDO node) {

    }

    @Override
    public void finishNode(String traceId, String nodeId, String status, String errorMessage, Date endTime, long durationMs) {

    }
}