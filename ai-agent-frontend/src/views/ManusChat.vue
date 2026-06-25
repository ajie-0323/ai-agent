<template>
  <div class="chat-page">
    <!-- 顶部导航 -->
    <header class="chat-header">
      <button class="back-btn" @click="$router.push('/')">← 返回</button>
      <h2>AI 超级智能体</h2>
    </header>

    <!-- 聊天记录区 -->
    <div class="messages" ref="messagesRef">
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        :class="['message', msg.role === 'user' ? 'message-user' : 'message-ai']"
      >
        <template v-if="msg.role === 'assistant'">
          <div class="message-avatar">🧠</div>
          <div class="message-content">
            <div class="message-bubble message-bubble-ai">{{ msg.content }}</div>
          </div>
        </template>
        <template v-else>
          <div class="message-content">
            <div class="message-bubble message-bubble-user">{{ msg.content }}</div>
          </div>
        </template>
      </div>

      <!-- AI 回复中：打字机效果 -->
      <div v-if="loading" class="message message-ai">
        <div class="message-avatar">🧠</div>
        <div class="message-content">
          <div class="message-bubble message-bubble-ai">
            <span v-if="currentAiContent === ''">AI 正在思考...</span>
            <span v-else>{{ currentAiContent }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区 -->
    <div class="input-area">
      <div class="input-wrapper">
        <input
          v-model="inputText"
          type="text"
          placeholder="请输入你的问题..."
          :disabled="loading"
          @keydown.enter="sendMessage"
        />
        <button :disabled="!inputText.trim() || loading" @click="sendMessage">
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onUnmounted } from "vue";
import { fetchSSE } from "../api/sse.js";
import { createTypewriter } from "../composables/useTypewriter.js";

const messages = ref([]);
const inputText = ref("");
const loading = ref(false);
const messagesRef = ref(null);

const currentAiContent = ref("");
let typewriter = null;
let abortController = null;

async function scrollToBottom() {
  await nextTick();
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
  }
}

function sendMessage() {
  const text = inputText.value.trim();
  if (!text || loading.value) return;

  messages.value.push({ role: "user", content: text });
  inputText.value = "";
  loading.value = true;
  currentAiContent.value = "";
  scrollToBottom();

  const url = "/api/ai/manus/chat?message=" + encodeURIComponent(text);

  abortController = new AbortController();
  currentAiContent.value = "";
  typewriter = createTypewriter(currentAiContent, 25);

  fetchSSE(url, {
    signal: abortController.signal,
    onMessage: (data) => {
      // 后端每完成一步才响应一次，每次收到 data 后追加一个换行分隔步骤
      typewriter.feed(data + "\n\n");
      scrollToBottom();
    },
    onError: (err) => {
      console.error("SSE error:", err);
      typewriter.complete();
      if (currentAiContent.value === "") {
        currentAiContent.value = "抱歉，连接出现了问题，请稍后重试。";
      }
      loading.value = false;
      abortController = null;
      scrollToBottom();
    },
    onComplete: () => {
      typewriter.complete();
      messages.value.push({ role: "assistant", content: currentAiContent.value });
      currentAiContent.value = "";
      loading.value = false;
      abortController = null;
      scrollToBottom();
    },
  });
}

onUnmounted(() => {
  if (typewriter) typewriter.cancel();
  if (abortController) abortController.abort();
});
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

/* ===== 顶部导航 ===== */
.chat-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--color-white);
  border-bottom: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
  z-index: 10;
}

.back-btn {
  background: none;
  border: none;
  font-size: var(--font-size-md);
  cursor: pointer;
  color: var(--color-primary);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  transition: background 0.2s;
}
.back-btn:hover {
  background: #f0f0ff;
}

.chat-header h2 {
  font-size: var(--font-size-lg);
  color: var(--color-text);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ===== 消息区 ===== */
.messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* --- 单条消息容器 --- */
.message {
  display: flex;
  gap: var(--spacing-sm);
  max-width: 70%;
}

.message-user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-ai {
  align-self: flex-start;
}

/* --- 头像 --- */
.message-avatar {
  width: var(--avatar-size);
  height: var(--avatar-size);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: var(--color-gradient);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

/* --- 消息内容容器 --- */
.message-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  min-width: 0;
}

/* --- 气泡 --- */
.message-bubble {
  padding: 10px 16px;
  border-radius: var(--radius-lg);
  line-height: 1.6;
  font-size: var(--font-size-sm);
  word-break: break-word;
  white-space: pre-wrap;
  text-align: left;
}

.message-bubble-user {
  background: var(--color-bubble-user);
  color: var(--color-bubble-user-text);
  border-bottom-right-radius: var(--radius-sm);
}

.message-bubble-ai {
  background: var(--color-bubble-ai);
  color: var(--color-bubble-ai-text);
  border-bottom-left-radius: var(--radius-sm);
  box-shadow: var(--shadow-sm);
}

/* ===== 输入区 ===== */
.input-area {
  flex-shrink: 0;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--color-white);
  border-top: 1px solid var(--color-border);
}

.input-wrapper {
  display: flex;
  gap: var(--spacing-sm);
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.input-wrapper input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  outline: none;
  transition: border-color 0.2s;
}
.input-wrapper input:focus {
  border-color: var(--color-primary);
}

.input-wrapper button {
  padding: 12px 28px;
  background: var(--color-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  cursor: pointer;
  transition: background 0.2s;
  white-space: nowrap;
}
.input-wrapper button:hover:not(:disabled) {
  background: var(--color-primary-dark);
}
.input-wrapper button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* ===== 响应式 ===== */

@media (max-width: 767px) {
  .chat-header {
    padding: var(--spacing-sm) var(--spacing-md);
  }
  .chat-header h2 {
    font-size: var(--font-size-md);
  }

  .messages {
    padding: var(--spacing-md);
  }

  .message {
    max-width: 85%;
  }

  .message-bubble {
    padding: 8px 14px;
    font-size: var(--font-size-sm);
  }

  .message-avatar {
    width: var(--avatar-size-sm);
    height: var(--avatar-size-sm);
    font-size: 14px;
  }

  .input-area {
    padding: var(--spacing-sm) var(--spacing-md);
  }
  .input-wrapper input {
    padding: 10px 14px;
    font-size: var(--font-size-sm);
  }
  .input-wrapper button {
    padding: 10px 20px;
  }
}

@media (min-width: 768px) and (max-width: 1024px) {
  .message {
    max-width: 75%;
  }
}
</style>