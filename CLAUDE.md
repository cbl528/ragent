# Ragent AI — 项目复刻指导文件

> 复刻源项目：https://github.com/nageoffer/ragent (E:\JavaProject\ragent)
> 当前项目：E:\Web\ragent

---

## 项目概览

Ragent 是一个企业级 Agentic RAG（检索增强生成）平台，覆盖从文档入库到智能问答的完整链路。

| 维度 | 详情 |
|------|------|
| 后端 | Java 17, Spring Boot 3.5.7, PostgreSQL + pgvector, Redis, RocketMQ, MyBatis-Plus, Sa-Token, Tika |
| 前端 | React 18, TypeScript, Vite 5, Tailwind CSS, shadcn/ui, Zustand |
| 模块 | framework（基础设施）、infra-ai（AI 模型抽象）、bootstrap（业务逻辑）、mcp-server（MCP 服务） |
| 源项目规模 | Java ~40000 行, TS/React ~18000 行, 20 张表, 22 个前端页面 |

## 模块架构

```
ragent/
├── pom.xml              # 父 POM：Java 17, Spring Boot 3.5.7, 统一依赖管理
├── bootstrap/            # 启动模块 + 业务逻辑
├── framework/            # 通用基础设施层（与业务无关的横切关注点）
├── infra-ai/             # AI 模型调用抽象层（Chat/Embedding/Rerank）
├── mcp-server/           # MCP 工具服务（独立部署进程）
└── console/              # 前端（当前为 Vue 3 脚手架，应替换为 React）
```

## 数据库设计（20 张表）

| 业务域 | 表名 |
|--------|------|
| 用户与会话 | t_user, t_conversation, t_conversation_summary, t_message, t_message_feedback, t_sample_question |
| 知识库 | t_knowledge_base, t_knowledge_document, t_knowledge_chunk, t_knowledge_document_chunk_log, t_knowledge_document_schedule, t_knowledge_document_schedule_exec |
| 意图与检索 | t_intent_node, t_query_term_mapping, t_rag_trace_run, t_rag_trace_node |
| 入库 Pipeline | t_ingestion_pipeline, t_ingestion_pipeline_node, t_ingestion_task, t_ingestion_task_node |
| 向量存储 | t_knowledge_vector（pgvector） |

---

## RAG 核心链路（8 个阶段）

```
用户提问
  → ① 加载会话记忆 (loadMemory)
  → ② 问题重写 + 拆分子问题 (rewriteQuery)
  → ③ 意图识别 — 树形分类 (resolveIntents)
  → ④ 歧义检测 — 置信度不足引导用户澄清 (handleGuidance) [短路1]
  → ⑤ 系统对话直接回复 (handleSystemOnly) [短路2]
  → ⑥ 多路检索 + MCP 工具调用 (retrieve)
  → ⑦ 空结果兜底 (handleEmptyRetrieval) [短路3]
  → ⑧ Prompt 组装 + LLM 流式生成 (streamRagResponse)
```

---

## 当前进度总览

```
framework/     ████████████████████░  95%  1 个 Bug
infra-ai/      ██████████████░░░░░░░  70%  EmbeddingClient 残缺, LLMService 无实现
bootstrap/     ████████░░░░░░░░░░░░░░  40%  DTO/Entity 齐全, 核心服务大多为空
mcp-server/    ███░░░░░░░░░░░░░░░░░░░  10%  只有空壳启动类
console/       ██░░░░░░░░░░░░░░░░░░░░   5%  Vue 空脚手架
SQL/配置       ░░░░░░░░░░░░░░░░░░░░░░   0%  无建表脚本, 无完整应用配置
总体           ██████░░░░░░░░░░░░░░░░  ~25%
```

---

## 待修复 Bug 清单

### ✅ BUG-1: AbstractException 字段赋值颠倒
- 文件: `framework/src/main/java/com/caobolun/framework/exception/AbstractException.java`
- 问题: `this.errorMessage = errorCode.code()` 和 `this.errorCode = errorCode.message()` 的值赋值反了
- 状态: **已修复** ✅

### ✅ BUG-2: RAGChatController SpEL 引用旧包名
- 文件: `bootstrap/src/main/java/com/caobolun/bootstrap/rag/controller/RAGChatController.java:23`
- 问题: `@IdempotentSubmit` 的 key 表达式引用 `com.nageoffer.ai.ragent.framework.context.UserContext`，应为 `com.caobolun.framework.context.UserContext`
- 状态: **已修复** ✅

### ✅ BUG-3: StreamTaskManager 缺少分号
- 文件: `bootstrap/src/main/java/com/caobolun/bootstrap/rag/handler/StreamTaskManager.java:47`
- 问题: addListener lambda 结尾缺少分号，编译不通过
- 状态: **已修复** ✅

### 🔴 BUG-4: AbstractOpenAIStyleEmbeddingClient 方法不完整
- 文件: `infra-ai/src/main/java/com/caobolun/infraai/embedding/AbstractOpenAIStyleEmbeddingClient.java`
- 问题: `doEmbed()` 方法没有 return 语句, `embedBatch()` 逻辑不完整
- 状态: **待修复**

### 🟡 BUG-5: 缺失类引用
- `DocumentSourceRequest` 引用的 `SourceType` 枚举不存在
- `ConversationMessageService` 引用的 `ConversationMessageBO`, `ConversationSummaryBO`, `ConversationMessageOrder` 不存在
- `ConversationService` 引用的 `ConversationCreateBO` 不存在
- 状态: **待修复**

### 🟡 BUG-6: 密码明文存储
- 文件: `AuthServiceImpl.java`, `UserServiceImpl.java`
- 问题: 密码未经过哈希直接存储到数据库
- 状态: **待修复**

---

## 复刻分步计划

### 🥇 第一阶段：修复 + 配置 + 建表（让项目能启动）⭐ 当前阶段

```
步骤 1.1: 修复 BUG-1 (AbstractException 字段赋值颠倒)
步骤 1.2: 修复 BUG-2 (SpEL 旧包名)
步骤 1.3: 修复 BUG-3 (StreamTaskManager 分号)
步骤 1.4: 修复 BUG-4 (EmbeddingClient 方法不完整)
步骤 1.5: 修复 BUG-5 (创建缺失的类: SourceType, ConversationMessageBO, ConversationSummaryBO, ConversationMessageOrder, ConversationCreateBO)
步骤 1.6: 编写 SQL 建表脚本 (t_user, t_conversation, t_conversation_summary, t_message 等)
步骤 1.7: 补全 bootstrap/application.yaml (数据源、Redis、AI Provider 配置)
步骤 1.8: 验证 mvn clean compile 通过
```

### 🥈 第二阶段：打通最小问答闭环（能做对话）

```
步骤 2.1: 实现 LLMService 接口 (模型路由 + 多 provider 调度)
步骤 2.2: 实现 RAGChatServiceImpl.streamChat() — 最小版本（LLM 直接回答，无检索）
步骤 2.3: 实现 StreamCallbackFactory（创建流式回调）
步骤 2.4: 完善 StreamTaskManager（取消任务逻辑）
步骤 2.5: 实现 ConversationService（会话 CRUD）
步骤 2.6: 实现 ConversationMessageService（消息存取/历史）
步骤 2.7: 实现 ConversationMemoryStore（JDBC 存储实现）
步骤 2.8: 实现 ConversationMemoryService（记忆加载 + 追加）
步骤 2.9: 实现 ConversationMemorySummaryService（对话摘要压缩）
步骤 2.10: 验证 SSE 流式问答可用
```

### 🥉 第三阶段：知识库 + 文档入库

```
步骤 3.1: 编写知识库相关建表 SQL (t_knowledge_base, t_knowledge_document, t_knowledge_chunk 等)
步骤 3.2: 实现 EmbeddingService（路由 + 具体实现）
步骤 3.3: 实现 DocumentParser（基于 Tika 的多格式解析）
步骤 3.4: 实现 ChunkingStrategy（固定大小 + 结构感知分块）
步骤 3.5: 实现 ChunkEmbeddingService（分块 → 向量化）
步骤 3.6: 实现 VectorStoreService（pgvector 写入 + 相似度检索）
步骤 3.7: 实现 KnowledgeBaseController / KnowledgeBaseService（知识库 CRUD）
步骤 3.8: 实现 KnowledgeDocumentController / KnowledgeDocumentService（文档上传）
步骤 3.9: 实现 KnowledgeChunkController（分块查看）
步骤 3.10: 验证文档上传 → 分块 → 向量化 → 检索链路可用
```

### 🏅 第四阶段：RAG 核心检索链路

```
步骤 4.1: 实现 IntentNode 管理（CRUD + 树结构 + Redis 缓存）
步骤 4.2: 实现 IntentClassifier（LLM 意图分类）
步骤 4.3: 实现 IntentResolver（意图解析 + 分数过滤 + 上限控制）
步骤 4.4: 实现 QueryRewriteService（问题重写 + 子问题拆分）
步骤 4.5: 实现 SearchChannel 体系（IntentDirectedSearchChannel + VectorGlobalSearchChannel）
步骤 4.6: 实现后处理链（DeduplicationPostProcessor + RerankPostProcessor）
步骤 4.7: 实现 RetrievalEngine（多通道调度 + 结果融合）
步骤 4.8: 实现 PromptTemplateLoader + Prompt 模板文件
步骤 4.9: 实现 RAGPromptService（场景选择 + 消息组装）
步骤 4.10: 实现 GuidanceService（歧义检测 + 引导生成）
步骤 4.11: 将完整 Stage 1-8 接入 RAGChatServiceImpl
步骤 4.12: 验证完整 RAG 问答链路可用
```

### 🎖️ 第五阶段：高级特性

```
步骤 5.1: 实现 MCP 工具调用（ParameterExtractor + MCP Client + Registry）
步骤 5.2: 实现 MCP Server 端 Executor（WeatherMcpExecutor, TicketMcpExecutor, SalesMcpExecutor）
步骤 5.3: 实现分布式公平限流（FairDistributedRateLimiter）
步骤 5.4: 实现 ChatQueueLimiter（排队限流）
步骤 5.5: 实现模型路由熔断（ModelHealthStore 三态熔断器）
步骤 5.6: 实现首包探测（多模型竞速）
步骤 5.7: 实现 IngestionPipeline（可编排入库流水线）
步骤 5.8: 实现 IngestionTask 管理
步骤 5.9: 实现定时文档刷新（调度 + 分布式锁）
```

### 🏆 第六阶段：全链路追踪 + 管理后台

```
步骤 6.1: 完善 RagTraceController（Trace 查询 API）
步骤 6.2: 完善 RagTraceQueryService
步骤 6.3: 实现 DashboardController + DashboardService
步骤 6.4: 实现 Admin 后台相关 API
```

### 🎯 第七阶段：前端（React 18 + TypeScript）

```
步骤 7.1: 替换 Vue 脚手架为 React 项目 (Vite 5 + React 18 + TypeScript + Tailwind + shadcn/ui)
步骤 7.2: 实现登录页 (LoginPage)
步骤 7.3: 实现聊天主界面 (ChatPage + 消息列表 + Markdown 渲染 + 输入框)
步骤 7.4: 实现会话列表侧边栏
步骤 7.5: 实现管理后台框架 (AdminLayout + 侧边导航)
步骤 7.6: 实现知识库管理页面
步骤 7.7: 实现意图树编辑页面
步骤 7.8: 实现入库 Pipeline 监控页面
步骤 7.9: 实现全链路 Trace 查看页面
步骤 7.10: 实现仪表板 Dashboard 页面
步骤 7.11: 实现用户管理页面
步骤 7.12: 实现系统设置页面
```

---

## 命令规则

- 当用户询问"进度"、"下一步"、"规划"、"复刻到哪了"等，按此文件汇报当前进度和下一步任务
- 当用户说"继续"、"开始下一步"时，从当前未完成的最小步骤开始执行
- 每完成一个步骤，更新对应状态标记（⬜ → ✅）
- 源项目参考路径：`E:\JavaProject\ragent`

## 关键技术难点

| 难点 | 涉及知识点 | 在项目中的位置 |
|------|-----------|---------------|
| 三态熔断器 | CLOSED→OPEN→HALF_OPEN 状态机 | ModelHealthStore |
| 分布式排队限流 | Redis ZSET + Lua 原子操作 + Pub/Sub | FairDistributedRateLimiter |
| 多线程池 + TTL 透传 | 8 个专用线程池 + TtlExecutors | bootstrap 配置 |
| 首包探测 | 装饰器模式 + 多模型竞速 | LlmFirstPacketProbe |
| 多路检索去重融合 | 策略模式 + 责任链后处理 | retrieve/ + postprocessor/ |
| 意图树 + 歧义引导 | 树形分类 + 置信度阈值 | intent/ |
| 会话记忆压缩 | 滑动窗口 + LLM 摘要生成 | memory/ |
