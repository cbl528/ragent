package com.caobolun.bootstrap.core.memory;

import com.caobolun.bootstrap.core.prompt.PromptTemplateLoader;
import com.caobolun.bootstrap.rag.config.MemoryProperties;
import com.caobolun.bootstrap.rag.service.ConversationGroupService;
import com.caobolun.bootstrap.rag.service.ConversationMessageService;
import com.caobolun.framework.convention.ChatMessage;
import com.caobolun.infraai.chat.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcConversationMemorySummaryService implements ConversationMemorySummaryService {

    private static final String SUMMARY_LOCK_PREFIX = "ragent:memory:summary:lock:";

    private final ConversationGroupService conversationGroupService;
    private final ConversationMessageService conversationMessageService;
    private final MemoryProperties memoryProperties;
    private final LLMService llmService;
    private final PromptTemplateLoader promptTemplateLoader;
    private final RedissonClient redissonClient;
    private final Executor memorySummaryExecutor;

    @Override
    public void compressIfNeeded(String conversationId, String userId, ChatMessage message) {

    }

    @Override
    public ChatMessage loadLatestSummary(String conversationId, String userId) {
        return null;
    }

    @Override
    public ChatMessage decorateIfNeeded(ChatMessage summary) {
        return null;
    }
}
