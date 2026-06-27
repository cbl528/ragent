package com.caobolun.infraai.embedding;


import com.caobolun.framework.exception.RemoteException;
import com.caobolun.infraai.enums.ModelCapability;
import com.caobolun.infraai.model.ModelRoutingExecutor;
import com.caobolun.infraai.model.ModelSelector;
import com.caobolun.infraai.model.ModelTarget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/* 路由式向量嵌入服务实现类
 * <p>
 * 该服务通过模型路由器选择合适的嵌入模型，并在执行失败时自动进行降级处理
 * 支持单文本和批量文本的向量化操作
 */
@Service
@Primary
public class RoutingEmbeddingService implements EmbeddingService {

    private final ModelSelector modelSelector; // 模型选择器
    private final ModelRoutingExecutor executor; // 模型路由执行器
    private final Map<String, EmbeddingClient> clientMap; // 模型客户端映射

    public RoutingEmbeddingService(ModelSelector modelSelector, ModelRoutingExecutor executor, List<EmbeddingClient> clients){
        this.modelSelector = modelSelector;
        this.executor = executor;
        this.clientMap = clients.stream()
                .collect(Collectors.toMap(EmbeddingClient::provider, Function.identity()));
    }


    /**
     * 单文本向量化
     * @param text 待向量化文本
     * @return 向量化结果
     */
    @Override
    public List<Float> embed(String text) {
        return executor.executeWithFallback(
                ModelCapability.EMBEDDING,
                modelSelector.selectEmbeddingCandidates(),
                this::resolveClient,
                (client, target) -> client.embed(text, target)
        );
    }

    /**
     * 根据目标选择客户端
     * @param target 模型目标
     * @return 选择的客户端
     */
    private EmbeddingClient resolveClient(ModelTarget target) {
        return clientMap.get(target.candidate().getProvider());
    }

    /**
     * 单文本向量化（指定模型）
     * @param text    待向量化文本
     * @param modelId 指定的模型ID
     * @return 向量化结果
     */
    @Override
    public List<Float> embed(String text, String modelId) {
        return executor.executeWithFallback(
                ModelCapability.EMBEDDING,
                List.of(resolveTarget(modelId)),
                this::resolveClient,
                (client, target) -> client.embed(text, target)
        );
    }

    /**
     * 根据模型ID选择目标
     * @param modelId 模型ID
     * @return 模型目标
     */
    private ModelTarget resolveTarget(String modelId) {
        if (!StringUtils.hasText(modelId)) {
            throw new RemoteException("Embedding 模型ID不能为空");
        }
        return modelSelector.selectEmbeddingCandidates().stream()
                .filter(target -> modelId.equals(target.id())) // 过滤出指定的模型目标
                .findFirst() // 找到第一个匹配的模型目标
                .orElseThrow(() -> new RemoteException("Embedding 模型不可用: " + modelId)); // 抛出异常表示没有可用的模型
    }

    /**
     * 批量文本向量化
     * @param texts 文本列表
     * @return 向量化结果
     */
    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        return executor.executeWithFallback(
                ModelCapability.EMBEDDING,
                modelSelector.selectEmbeddingCandidates(),
                this::resolveClient,
                (client, target) -> client.embedBatch(texts, target)
        );
    }

    /**
     * 批量文本向量化（指定模型）
     * @param texts   文本列表
     * @param modelId 指定的模型ID
     * @return 向量化结果
     */
    @Override
    public List<List<Float>> embedBatch(List<String> texts, String modelId) {
        return executor.executeWithFallback(
                ModelCapability.EMBEDDING,
                List.of(resolveTarget(modelId)),
                this::resolveClient,
                (client, target) -> client.embedBatch(texts, target)
        );
    }
}