<template>
  <div class="agent-page">
    <header class="agent-header">
      <button class="btn-icon" @click="$router.push('/')" title="返回">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
      </button>
      <div class="agent-info">
        <div class="agent-name-row">
          <span class="agent-dot" :class="loading ? 'dot-thinking' : 'dot-idle'"></span>
          <span class="agent-name">Manus</span>
          <span class="agent-badge">超级智能体</span>
        </div>
        <span class="agent-status">{{ loading ? "思考中..." : "空闲" }}</span>
      </div>
      <div class="header-actions">
        <button class="btn-icon" @click="showFilePanel = !showFilePanel" title="文件" :class="{ active: showFilePanel }">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        </button>
        <button class="btn-icon" @click="clearChat" title="清除对话">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
        </button>
      </div>
    </header>

    <!-- 文件面板 -->
    <div v-if="showFilePanel" class="file-panel-overlay" @click="showFilePanel = false"></div>
    <div v-if="showFilePanel" class="file-panel">
      <div class="file-panel-header">
        <h3>文件列表</h3>
        <button class="btn-icon-sm" @click="showFilePanel = false">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
      <div class="file-panel-body">
        <div v-if="Object.keys(fileGroups).length === 0" class="file-empty">暂无可用的文件</div>
        <div v-for="(group, key) in fileGroups" :key="key" class="file-group">
          <div class="file-group-title">{{ groupLabels[key] || key }}</div>
          <div v-for="f in group" :key="f.name" class="file-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
            <div class="file-item-info" @click="downloadFile(f.name)">
              <span class="file-item-name">{{ f.name }}</span>
              <span class="file-item-size">{{ f.sizeReadable }}</span>
            </div>
            <button class="file-del-btn" @click.stop="deleteFile(f.name)" title="删除">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
            </button>
          </div>
        </div>
      </div>
      <div class="file-panel-footer">
        <button class="btn-refresh" @click="fetchFileList">刷新列表</button>
      </div>
    </div>

    <!-- 消息区 -->
    <div class="messages" ref="messagesRef">
      <div v-if="messages.length === 0 && !loading" class="welcome">
        <div class="welcome-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        </div>
        <h2 class="welcome-title">Manus 超级智能体</h2>
        <p class="welcome-desc">试试下面的例子：</p>
        <div class="welcome-suggestions">
          <div class="suggestion" @click="quickSend('搜索今天的科技热点新闻')"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>搜索新闻</div>
          <div class="suggestion" @click="quickSend('写一个 Python 脚本读取当前目录下所有 txt 文件')"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>编写代码</div>
          <div class="suggestion" @click="quickSend('帮我写一份上海三日游的计划，保存到文件')"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>制定计划</div>
          <div class="suggestion" @click="quickSend('写一份关于人工智能发展的报告，生成 PDF')"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>生成报告</div>
        </div>
      </div>

      <div v-for="(msg, idx) in messages" :key="idx" :class="['msg', msg.role === 'user' ? 'msg-user' : 'msg-assistant']">
        <div class="msg-avatar" :class="msg.role">
          <svg v-if="msg.role === 'assistant'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        </div>
        <div class="msg-body">
          <div class="msg-name">{{ msg.role === 'assistant' ? 'Manus' : '你' }}</div>
          <div class="msg-bubble" v-html="renderContent(msg.content)"></div>
        </div>
      </div>

      <div v-if="loading" class="msg msg-assistant">
        <div class="msg-avatar assistant">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        </div>
        <div class="msg-body">
          <div class="msg-name">Manus</div>
          <div class="msg-bubble">
            <span v-if="!currentAiContent" class="thinking-indicator"><span class="dot-pulse"></span><span class="dot-pulse"></span><span class="dot-pulse"></span></span>
            <span v-else v-html="renderContent(currentAiContent)"></span>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <div class="input-bar">
        <button class="btn-upload" @click="triggerUpload" title="上传文件" :disabled="loading">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
        </button>
        <input ref="inputRef" v-model="inputText" type="text" placeholder="输入你的问题..." :disabled="loading" @keydown.enter="sendMessage" />
        <button v-if="!loading" class="btn-send" :disabled="!inputText.trim()" @click="sendMessage" title="发送">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
        </button>
        <button v-else class="btn-stop" @click="stopGeneration" title="停止">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="6" y="6" width="12" height="12" rx="2"/></svg>
        </button>
      </div>
      <div v-if="uploading" class="upload-progress"><span class="upload-spinner"></span><span>上传中...</span></div>
      <div v-if="uploadSuccess" class="upload-success"><span>已上传：{{ uploadedFileName }}</span><button class="btn-upload-insert" @click="insertUploadRef">插入引用</button></div>
    </div>

    <input ref="fileInputRef" type="file" style="display:none" @change="handleFileSelected" />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted, computed } from "vue"
import { fetchSSE } from "../api/sse.js"
import { createTypewriter } from "../composables/useTypewriter.js"
import request from "../api/index.js"

const messages = ref([])
const inputText = ref("")
const loading = ref(false)
const messagesRef = ref(null)
const inputRef = ref(null)
const fileInputRef = ref(null)
const showFilePanel = ref(false)
const files = ref([])
const uploading = ref(false)
const uploadSuccess = ref(false)
const uploadedFileName = ref("")
const currentAiContent = ref("")

let typewriter = null
let abortController = null

// ====== 会话 ID（localStorage 持久化）======
const STORAGE_KEY = "manus_chat_id"
const MSG_KEY = "manus_messages"
const chatId = ref(localStorage.getItem(STORAGE_KEY) || "manus_" + Date.now().toString(36) + "_" + Math.random().toString(36).slice(2, 8))

// 保存 chatId 到 localStorage
localStorage.setItem(STORAGE_KEY, chatId.value)

const groupLabels = { agent_files: "智能体创建", pdf_files: "PDF 文档", uploads: "用户上传", downloads: "网络下载" }
const fileGroups = computed(() => {
  const groups = {}
  for (const [key, list] of Object.entries(files.value)) {
    if (Array.isArray(list) && list.length > 0) groups[key] = list
  }
  return groups
})

function quickSend(text) { inputText.value = text; sendMessage() }
async function scrollToBottom() { await nextTick(); if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight }

function saveMessages() {
  try { localStorage.setItem(MSG_KEY, JSON.stringify(messages.value)) } catch {}
}

function loadMessages() {
  try {
    const saved = localStorage.getItem(MSG_KEY)
    if (saved) messages.value = JSON.parse(saved)
  } catch {}
}

function clearChat() {
  if (loading.value) return
  messages.value = []
  inputText.value = ""
  localStorage.removeItem(MSG_KEY)
  // 生成新会话 ID
  chatId.value = "manus_" + Date.now().toString(36) + "_" + Math.random().toString(36).slice(2, 8)
  localStorage.setItem(STORAGE_KEY, chatId.value)
}

// ====== 历史加载 ======
async function loadHistory() {
  try {
    const res = await request.get("/ai/manus/history", { params: { chatId: chatId.value } })
    if (res && Array.isArray(res) && res.length > 0) {
      messages.value = res
      saveMessages()
      await scrollToBottom()
    }
  } catch (err) {
    console.log("No history:", err.message)
  }
}

// ====== 文件操作 ======
function triggerUpload() { fileInputRef.value?.click() }

async function handleFileSelected(e) {
  const file = e.target.files[0]
  if (!file) return
  uploading.value = true
  uploadSuccess.value = false
  const formData = new FormData()
  formData.append("file", file)
  try {
    const res = await request.post("/file/upload", formData, { headers: { "Content-Type": "multipart/form-data" } })
    uploadedFileName.value = res.originalName || file.name
    uploadSuccess.value = true
    fetchFileList()
  } catch (err) {
    console.error("Upload failed:", err)
    alert("Upload failed: " + err.message)
  } finally {
    uploading.value = false
    e.target.value = ""
  }
}

function insertUploadRef() {
  inputText.value = "我刚刚上传了一个文件「" + uploadedFileName.value + "」，请帮我处理一下。"
  uploadSuccess.value = false
  inputRef.value?.focus()
}

function downloadFile(fileName) {
  window.open("/api/file/download/" + encodeURIComponent(fileName), "_blank")
}

async function deleteFile(fileName) {
  if (!confirm("确定删除 " + fileName + " ？")) return
  try {
    await request.delete("/file/" + encodeURIComponent(fileName))
    fetchFileList()
  } catch (err) {
    console.error("Delete failed:", err)
    alert("Delete failed: " + err.message)
  }
}

async function fetchFileList() {
  try {
    const res = await request.get("/file/list")
    files.value = res || {}
  } catch (err) {
    console.error("Fetch file list failed:", err)
  }
}

// ====== 停止生成 ======
function stopGeneration() {
  if (abortController) {
    abortController.abort()
  }
}

// ====== 渲染 ======
function renderContent(text){
  if (!text) return ""
  // Normalize literal backslash-n to actual newlines
  text = text.replace(/\\n/g, "\n")
  // Collapse 3+ consecutive blank lines to 1
  text = text.replace(/\n{3,}/g, "\n\n")

  const parts = []
  let lastIdx = 0
  const codeBlockRegex = /```(\w*)\n?([\s\S]*?)```/g
  let match
  while ((match = codeBlockRegex.exec(text)) !== null) {
    if (match.index > lastIdx) parts.push({ type: "text", content: text.slice(lastIdx, match.index) })
    parts.push({ type: "code", lang: match[1] || "", content: match[2].trim() })
    lastIdx = match.index + match[0].length
  }
  if (lastIdx < text.length) parts.push({ type: "text", content: text.slice(lastIdx) })
  if (parts.length === 0) parts.push({ type: "text", content: text })

  return parts.map(part => {
    if (part.type === "code") {
      return `<pre class="code-block${part.lang ? " lang-" + escapeHtml(part.lang) : ""}"><code>${escapeHtml(part.content)}</code></pre>`
    }
    let html = escapeHtml(part.content)
    html = html.replace(/`([^`]+)`/g, '<code class="code-inline">$1</code>')
    html = html.replace(/\n/g, "<br>")
    return html
  }).join("")
}

function escapeHtml(str){
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
}

// ====== 发送消息 ======
function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return
  messages.value.push({ role: "user", content: text })
  saveMessages()
  inputText.value = ""
  loading.value = true
  currentAiContent.value = ""
  scrollToBottom()

  const url = "/api/ai/manus/chat?message=" + encodeURIComponent(text) + "&chatId=" + encodeURIComponent(chatId.value)
  abortController = new AbortController()
  currentAiContent.value = ""
  typewriter = createTypewriter(currentAiContent, 15)

  fetchSSE(url, {
    signal: abortController.signal,
    onMessage: (data) => { typewriter.feed(data + "\n"); scrollToBottom() },
    onError: (err) => {
      console.error("SSE error:", err); typewriter.complete()
      if (currentAiContent.value === "") currentAiContent.value = "连接出现问题，请稍后重试。"
      loading.value = false; abortController = null; scrollToBottom()
    },
    onComplete: () => {
      typewriter.complete()
      if (currentAiContent.value) {
        messages.value.push({ role: "assistant", content: currentAiContent.value })
      }
      saveMessages()
      currentAiContent.value = ""; loading.value = false; abortController = null
      scrollToBottom()
      fetchFileList()
    },
  })
}

onMounted(async () => {
  inputRef.value?.focus()
  loadMessages()
  if (messages.value.length === 0) await loadHistory()
  await scrollToBottom()
  fetchFileList()
})

onUnmounted(() => {
  if (typewriter) typewriter.cancel()
  if (abortController) abortController.abort()
})
</script>

<style scoped>
.agent-page { height: 100vh; display: flex; flex-direction: column; background: var(--color-bg); position: relative; }
.agent-header { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); background: var(--color-bg-light); border-bottom: 1px solid var(--color-border); flex-shrink: 0; z-index: 10; }
.btn-icon { display: flex; align-items: center; justify-content: center; width: 34px; height: 34px; background: none; border: 1px solid transparent; border-radius: var(--radius-md); color: var(--color-text-secondary); cursor: pointer; transition: all var(--transition-fast); }
.btn-icon:hover, .btn-icon.active { background: var(--color-bg-hover); color: var(--color-text); border-color: var(--color-border); }
.btn-icon.active { background: var(--color-primary-light); color: var(--color-primary); border-color: var(--color-primary); }
.header-actions { display: flex; gap: var(--space-1); }
.agent-info { flex: 1; min-width: 0; }
.agent-name-row { display: flex; align-items: center; gap: var(--space-2); }
.agent-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.dot-idle { background: var(--color-success); }
.dot-thinking { background: var(--color-agent-thinking); animation: pulse 1.2s ease-in-out infinite; }
.agent-name { font-size: var(--text-base); font-weight: 600; color: var(--color-text); }
.agent-badge { font-size: 10px; font-weight: 500; padding: 1px 6px; border-radius: var(--radius-sm); background: var(--color-primary-light); color: var(--color-primary); letter-spacing: 0.02em; }
.agent-status { font-size: var(--text-xs); color: var(--color-text-muted); }

/* File panel */
.file-panel-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.3); z-index: 20; animation: fadeIn 0.2s ease; }
.file-panel { position: fixed; top: 0; right: 0; bottom: 0; width: 340px; max-width: 90vw; background: var(--color-bg-light); border-left: 1px solid var(--color-border); z-index: 21; display: flex; flex-direction: column; animation: slideIn 0.25s ease; box-shadow: -4px 0 20px rgba(0,0,0,0.08); }
@keyframes slideIn { from { transform: translateX(100%); } to { transform: translateX(0); } }
.file-panel-header { display: flex; align-items: center; justify-content: space-between; padding: var(--space-4); border-bottom: 1px solid var(--color-border); }
.file-panel-header h3 { font-size: var(--text-base); font-weight: 600; }
.btn-icon-sm { display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: none; border: none; border-radius: var(--radius-sm); color: var(--color-text-muted); cursor: pointer; }
.btn-icon-sm:hover { background: var(--color-bg-hover); color: var(--color-text); }
.file-panel-body { flex: 1; overflow-y: auto; padding: var(--space-3); }
.file-empty { text-align: center; padding: var(--space-8); color: var(--color-text-muted); font-size: var(--text-sm); }
.file-group { margin-bottom: var(--space-4); }
.file-group-title { font-size: var(--text-xs); font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 0.05em; padding: 0 var(--space-2); margin-bottom: var(--space-2); }
.file-item { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2); border-radius: var(--radius-md); transition: background var(--transition-fast); color: var(--color-text-secondary); }
.file-item:hover { background: var(--color-bg-hover); }
.file-item svg { flex-shrink: 0; }
.file-item-info { flex: 1; min-width: 0; cursor: pointer; }
.file-item-info:hover { color: var(--color-primary); }
.file-item-name { font-size: var(--text-sm); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.file-item-size { font-size: var(--text-xs); color: var(--color-text-muted); }
.file-del-btn { display: flex; align-items: center; justify-content: center; width: 26px; height: 26px; background: none; border: none; border-radius: var(--radius-sm); color: var(--color-text-muted); cursor: pointer; opacity: 0; transition: all var(--transition-fast); }
.file-item:hover .file-del-btn { opacity: 1; }
.file-del-btn:hover { background: #fef2f2; color: var(--color-danger); }
.file-panel-footer { padding: var(--space-3); border-top: 1px solid var(--color-border); }
.btn-refresh { width: 100%; padding: var(--space-2); background: var(--color-bg); border: 1px solid var(--color-border); border-radius: var(--radius-md); font-size: var(--text-sm); color: var(--color-text-secondary); cursor: pointer; }
.btn-refresh:hover { background: var(--color-bg-hover); }

.messages { flex: 1; overflow-y: auto; padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.welcome { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; animation: slideUp 0.4s ease; padding: var(--space-8) 0; }
.welcome-icon { width: 56px; height: 56px; display: flex; align-items: center; justify-content: center; background: var(--color-primary-light); color: var(--color-primary); border-radius: var(--radius-xl); margin-bottom: var(--space-4); }
.welcome-title { font-size: var(--text-xl); font-weight: 700; color: var(--color-text); margin-bottom: var(--space-2); }
.welcome-desc { font-size: var(--text-sm); color: var(--color-text-secondary); margin-bottom: var(--space-6); }
.welcome-suggestions { display: flex; flex-wrap: wrap; gap: var(--space-2); justify-content: center; max-width: 480px; }
.suggestion { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-3); background: var(--color-bg-light); border: 1px solid var(--color-border); border-radius: var(--radius-md); font-size: var(--text-sm); color: var(--color-text-secondary); cursor: pointer; transition: all var(--transition-fast); white-space: nowrap; }
.suggestion:hover { border-color: var(--color-primary); color: var(--color-primary); background: var(--color-primary-light); }
.msg { display: flex; gap: var(--space-3); max-width: 800px; width: 100%; margin: 0 auto; animation: slideUp 0.25s ease; }
.msg-user { flex-direction: row-reverse; }
.msg-avatar { width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.msg-avatar.assistant { background: var(--color-primary); color: white; }
.msg-avatar.user { background: var(--color-bg-hover); color: var(--color-text-secondary); border: 1px solid var(--color-border); }
.msg-body { display: flex; flex-direction: column; gap: var(--space-1); min-width: 0; max-width: calc(100% - 44px); }
.msg-user .msg-body { align-items: flex-end; }
.msg-name { font-size: var(--text-xs); font-weight: 600; color: var(--color-text-muted); padding: 0 var(--space-1); }
.msg-bubble { padding: var(--space-3) var(--space-4); font-size: var(--text-sm); line-height: 1.7; word-break: break-word; text-align: left; }
.msg-assistant .msg-bubble { background: var(--color-bubble-ai); border: 1px solid var(--color-bubble-ai-border); border-radius: 0 var(--radius-lg) var(--radius-lg) var(--radius-lg); color: var(--color-bubble-ai-text); box-shadow: var(--shadow-sm); }
.msg-user .msg-bubble { background: var(--color-bubble-user); border-radius: var(--radius-lg) 0 var(--radius-lg) var(--radius-lg); color: var(--color-bubble-user-text); }
:deep(.code-block) { background: var(--color-terminal-bg); color: var(--color-terminal-text); padding: var(--space-3) var(--space-4); border-radius: var(--radius-md); font-family: var(--font-mono); font-size: var(--text-xs); line-height: 1.6; overflow-x: auto; margin: var(--space-2) 0; white-space: pre; border-left: 3px solid var(--color-primary); }
:deep(.code-inline) { background: var(--color-bg-hover); padding: 1px 5px; border-radius: var(--radius-sm); font-family: var(--font-mono); font-size: 0.9em; color: #e11d48; border: 1px solid var(--color-border); }
.thinking-indicator { display: flex; gap: 4px; padding: 4px 0; }
.dot-pulse { width: 6px; height: 6px; border-radius: 50%; background: var(--color-text-muted); animation: pulse 1.4s ease-in-out infinite; }
.dot-pulse:nth-child(2) { animation-delay: 0.2s; }
.dot-pulse:nth-child(3) { animation-delay: 0.4s; }
.input-area { flex-shrink: 0; padding: var(--space-3) var(--space-4) var(--space-4); background: var(--color-bg-light); border-top: 1px solid var(--color-border); }
.input-bar { display: flex; align-items: center; gap: var(--space-2); max-width: 800px; margin: 0 auto; width: 100%; background: var(--color-card); border: 1px solid var(--color-border); border-radius: var(--radius-lg); padding: var(--space-1); transition: border-color var(--transition-fast); }
.input-bar:focus-within { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(59,130,246,0.1); }
.input-bar input { flex: 1; padding: var(--space-2) var(--space-3); border: none; background: transparent; font-size: var(--text-sm); font-family: var(--font-sans); outline: none; color: var(--color-text); }
.input-bar input::placeholder { color: var(--color-text-muted); }
.btn-upload { display: flex; align-items: center; justify-content: center; width: 34px; height: 34px; border: none; border-radius: var(--radius-md); background: transparent; color: var(--color-text-muted); cursor: pointer; transition: all var(--transition-fast); flex-shrink: 0; }
.btn-upload:hover:not(:disabled) { background: var(--color-bg-hover); color: var(--color-text); }
.btn-upload:disabled { cursor: not-allowed; opacity: 0.5; }
.btn-send { display: flex; align-items: center; justify-content: center; width: 34px; height: 34px; border: none; border-radius: var(--radius-md); background: var(--color-primary); color: white; cursor: pointer; transition: all var(--transition-fast); flex-shrink: 0; }
.btn-send:hover:not(:disabled) { background: var(--color-primary-hover); }
.btn-send:disabled { background: var(--color-bg-hover); color: var(--color-text-muted); cursor: not-allowed; }
.btn-stop { display: flex; align-items: center; justify-content: center; width: 34px; height: 34px; border: none; border-radius: var(--radius-md); background: var(--color-danger); color: white; cursor: pointer; transition: all var(--transition-fast); flex-shrink: 0; }
.btn-stop:hover { background: #dc2626; }
.upload-progress, .upload-success { display: flex; align-items: center; gap: var(--space-2); max-width: 800px; margin: var(--space-2) auto 0; font-size: var(--text-xs); color: var(--color-text-muted); }
.upload-progress .upload-spinner { width: 12px; height: 12px; border: 2px solid var(--color-border); border-top-color: var(--color-primary); border-radius: 50%; animation: spin 0.6s linear infinite; }
.upload-success { color: var(--color-success); }
.btn-upload-insert { margin-left: auto; padding: 2px 10px; background: var(--color-primary-light); color: var(--color-primary); border: 1px solid var(--color-primary-subtle); border-radius: var(--radius-sm); font-size: var(--text-xs); cursor: pointer; }
.btn-upload-insert:hover { background: var(--color-primary); color: white; }
@media (max-width: 640px) {
  .agent-header { padding: var(--space-2) var(--space-3); }
  .messages { padding: var(--space-4); }
  .msg { max-width: 100%; }
  .msg-bubble { padding: var(--space-2) var(--space-3); }
  .input-area { padding: var(--space-2) var(--space-3) var(--space-3); }
  .welcome-suggestions { flex-direction: column; align-items: stretch; }
  .suggestion { justify-content: center; }
}
</style>
