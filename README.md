# 🤖 AI Agent 平台

基于 **Spring Boot + Vue 3** 的 AI 智能体平台，提供 AI 恋爱大师和 AI 超级智能体两个应用，支持流式对话交互。

---

## 📋 目录

- [技术栈](#-技术栈)
- [环境要求](#-环境要求)
- [快速开始](#-快速开始)
  - [后端启动](#1-后端启动)
  - [前端启动](#2-前端启动)
- [使用说明](#-使用说明)
  - [主页](#主页)
  - [AI 恋爱大师](#ai-恋爱大师)
  - [AI 超级智能体](#ai-超级智能体)
- [API 接口](#-api-接口)
- [项目结构](#-项目结构)

---

## 🛠️ 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 3 + Spring AI | Java 17+ |
| 前端框架 | Vue 3 + Vite | JavaScript |
| 请求库 | Axios | HTTP 请求 |
| 路由 | Vue Router 4 | 前端路由 |
| 流式通信 | SSE (Server-Sent Events) | AI 对话实时流式输出 |
| AI 模型 | DashScope (通义千问) | 大语言模型 |
| 向量数据库 | PGVector | 知识库检索 |

---

## 📦 环境要求

- **JDK 17+**
- **Maven 3.8+**（或使用项目内置的 `mvnw`）
- **Node.js 18+**
- **npm 9+**
- **PostgreSQL + PGVector**（可选，用于 RAG 知识库）
- **DashScope API Key**（阿里通义千问）

---

## 🚀 快速开始

### 1. 后端启动

```bash
# 1. 配置 API Key（必须）
# 复制配置模板
cp src/main/resources/application.yml src/main/resources/application-local.yml
# 编辑 application-local.yml，填入你的 API Key：
# dashscope:
#   api-key: sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# 2. 编译项目
./mvnw compile

# 3. 启动后端服务（默认端口 8123）
./mvnw spring-boot:run

# 或打包后运行
./mvnw package -DskipTests
java -jar target/ai-agent-0.0.1-SNAPSHOT.jar
```

> 💡 后端启动后访问 `http://localhost:8123/api/health` 验证服务是否正常运行。

### 2. 前端启动

```bash
# 1. 进入前端目录
cd ai-agent-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器（默认端口 5173）
npm run dev

# 4. 构建生产版本
npm run build
```

> 💡 前端开发服务器已配置代理，`/api` 开头的请求会自动转发到后端 `http://localhost:8123`。

启动后打开浏览器访问 **http://localhost:5173** 即可进入应用。

---

## 📖 使用说明

### 主页

进入应用后看到应用选择页面，展示两个 AI 应用卡片：

- **💕 AI 恋爱大师** — 智能分析情感问题，提供专业恋爱建议
- **🧠 AI 超级智能体** — 通用 AI 助手，支持工具调用完成各类任务

点击卡片即可进入对应的聊天界面。

### AI 恋爱大师

![AI 恋爱大师](https://img.shields.io/badge/AI-%E6%81%8B%E7%88%B1%E5%A4%A7%E5%B8%88-pink)

- **会话管理**：进入页面自动生成唯一的 `chatId`，用于区分不同会话
- **流式对话**：输入问题后按回车或点击发送，AI 回复以**打字机效果**逐字显示
- **功能特点**：
  - 基于 RAG 知识库的恋爱问答
  - 多轮对话记忆
  - 文件操作工具（保存恋爱攻略等）

### AI 超级智能体

![AI 超级智能体](https://img.shields.io/badge/AI-%E8%B6%85%E7%BA%A7%E6%99%BA%E8%83%BD%E4%BD%93-blue)

- **通用助手**：完成搜索、文件操作、代码执行等多种任务
- **步骤输出**：后端每完成一步操作会推送一次结果，前端每步之间自动换行分隔
- **打字机效果**：同恋爱大师，内容逐字显示
- **工具能力**：
  - 🌐 网页搜索
  - 📄 文件操作（读写、下载）
  - 🖥️ 终端命令执行
  - 📸 网页截图

---

## 🔌 API 接口

| 接口 | 方法 | 说明 | 参数 |
|------|------|------|------|
| `/api/ai/love_app/chat/sse` | GET | 恋爱大师流式对话 | `message`（消息内容）、`chatId`（会话ID） |
| `/api/ai/manus/chat` | GET | 超级智能体流式对话 | `message`（消息内容） |
| `/api/health` | GET | 健康检查 | 无 |

两个对话接口均使用 **SSE（Server-Sent Events）** 协议，实时推送 AI 回复内容。

---

## 📁 项目结构

```
ai-agent/
├── ai-agent-frontend/          # 前端项目（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/               # API 请求层（Axios + SSE）
│   │   ├── composables/       # 组合式函数（打字机效果）
│   │   ├── router/            # 路由配置
│   │   └── views/             # 页面组件
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── src/                        # 后端项目（Spring Boot）
│   ├── main/java/com/donggua/aiagent/
│   │   ├── agent/             # AI 智能体核心
│   │   ├── app/               # 应用层（恋爱大师等）
│   │   ├── controller/        # REST 控制器
│   │   ├── rag/               # RAG 知识库检索
│   │   ├── tools/             # 工具注册与实现
│   │   ├── chatmemory/        # 对话记忆
│   │   └── config/            # 配置（CORS 等）
│   └── main/resources/
│       ├── application.yml    # 主配置
│       └── document/          # 恋爱知识库文档
│
├── pom.xml                    # Maven 配置
├── .gitignore
└── README.md
```

---

## ⚙️ 配置说明

### 后端配置 (`application-local.yml`)

```yaml
spring:
  ai:
    dashscope:
      api-key: sk-你的API密钥
      chat:
        options:
          model: qwen-plus

# PGVector 配置（可选，不配置则使用本地文件记忆）
# spring:
#   datasource:
#     url: jdbc:postgresql://localhost:5432/ai_agent
#     username: postgres
#     password: your_password
```

### 前端配置

前端代理配置在 `ai-agent-frontend/vite.config.js` 中，默认代理 `/api` 到 `http://localhost:8123`，如后端端口有变化请同步修改。

---

## 📝 注意事项

1. **API Key**：使用前必须配置 DashScope API Key，否则 AI 功能不可用
2. **端口冲突**：默认后端端口 8123，前端端口 5173，如有冲突在配置文件中修改
3. **PGVector**：如果未配置 PostgreSQL，系统会使用本地文件存储对话记忆
4. **测试**：首次使用建议先访问 `/api/health` 确认后端正常运行

---

## 📄 开源协议

本项目仅供学习参考。