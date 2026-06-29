package com.caobolun.bootstrap.core.rewrite;

import com.caobolun.bootstrap.core.prompt.PromptTemplateLoader;
import com.caobolun.bootstrap.rag.config.RAGConfigProperties;
import com.caobolun.framework.convention.ChatMessage;
import com.caobolun.infraai.chat.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MultiQuestionRewriteService implements QueryRewriteService {

    private final LLMService llmService;
    private final RAGConfigProperties ragConfigProperties;
    private final QueryTermMappingService queryTermMappingService;
    private final PromptTemplateLoader promptTemplateLoader;

    @Override
    public String rewrite(String userQuestion) {
        return "";
    }

    @Override
    public RewriteResult rewriteWithSplit(String userQuestion) {
        return QueryRewriteService.super.rewriteWithSplit(userQuestion);
    }

    @Override
    public RewriteResult rewriteWithSplit(String userQuestion, List<ChatMessage> history) {
        return QueryRewriteService.super.rewriteWithSplit(userQuestion, history);
    }
}
