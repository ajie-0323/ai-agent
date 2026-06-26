<template>
  <div class="home">
    <!-- 顶部 -->
    <header class="home-header">
      <div class="header-content">
        <div class="brand">
          <div class="brand-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4c0 2-2 4-4 4s-4-2-4-4a4 4 0 0 1 4-4Z"/>
              <path d="M12 14c-4 0-7 2-7 5v1h14v-1c0-3-3-5-7-5Z"/>
              <path d="M17 10c2 0 4 1 4 3v1h-2"/>
              <path d="M7 10c-2 0-4 1-4 3v1h2"/>
            </svg>
          </div>
          <div class="brand-text">
            <h1>AI Agent</h1>
            <span class="brand-badge">v0.1</span>
          </div>
        </div>
        <p class="header-desc">选择智能体开始对话</p>
      </div>
    </header>

    <!-- 智能体列表 -->
    <main class="agent-list">
      <div class="agent-card" @click="$router.push('/manus-chat')">
        <div class="card-status status-active"></div>
        <div class="card-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
        </div>
        <div class="card-body">
          <div class="card-title-row">
            <h3>Manus</h3>
            <span class="card-tag tag-core">核心</span>
          </div>
          <p class="card-desc">通用 AI 超级智能体，可以调用多种工具完成搜索、编程、文件操作、数据分析等复杂任务</p>
          <div class="card-meta">
            <span class="meta-item">
              <span class="meta-dot dot-active"></span>
              在线
            </span>
            <span class="meta-item">11 个工具</span>
            <span class="meta-item">支持规划</span>
          </div>
        </div>
        <div class="card-arrow">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </div>
      </div>

      <div class="agent-card" @click="$router.push('/love-chat')">
        <div class="card-status status-active"></div>
        <div class="card-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/>
          </svg>
        </div>
        <div class="card-body">
          <div class="card-title-row">
            <h3>恋爱大师</h3>
            <span class="card-tag tag-app">应用</span>
          </div>
          <p class="card-desc">专业情感顾问，基于 RAG 知识库提供个性化的恋爱分析与建议</p>
          <div class="card-meta">
            <span class="meta-item">
              <span class="meta-dot dot-active"></span>
              在线
            </span>
            <span class="meta-item">RAG 知识库</span>
            <span class="meta-item">多轮对话</span>
          </div>
        </div>
        <div class="card-arrow">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </div>
      </div>
    </main>

    <!-- 底部信息 -->
    <footer class="home-footer">
      <span>基于 Spring AI Alibaba + DashScope</span>
      <span class="footer-dot">·</span>
      <span>OpenManus 架构参考</span>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--space-12) var(--space-6);
  background: var(--color-bg);
  animation: fadeIn 0.3s ease;
}

/* ===== 顶部 ===== */
.home-header {
  width: 100%;
  max-width: 600px;
  margin-bottom: var(--space-10);
}

.header-content {
  text-align: center;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.brand-icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-lg);
}

.brand-text {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
}

.brand-text h1 {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.brand-badge {
  font-size: var(--text-xs);
  font-weight: 500;
  color: var(--color-text-muted);
  background: var(--color-bg-hover);
  padding: 1px 8px;
  border-radius: var(--radius-full);
}

.header-desc {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

/* ===== 智能体列表 ===== */
.agent-list {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.agent-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--color-card);
  border: 1px solid var(--color-card-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
  position: relative;
}

.agent-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.agent-card:active {
  transform: translateY(0);
}

/* 状态指示灯 */
.card-status {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-active {
  background: var(--color-success);
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
}

/* 图标区 */
.card-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary-light);
  color: var(--color-primary);
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

/* 内容区 */
.card-body {
  flex: 1;
  min-width: 0;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-1);
}

.card-title-row h3 {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--color-text);
}

.card-tag {
  font-size: 10px;
  font-weight: 600;
  padding: 0 6px;
  border-radius: var(--radius-sm);
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.tag-core {
  background: var(--color-primary-subtle);
  color: var(--color-primary);
}

.tag-app {
  background: #fef3c7;
  color: #d97706;
}

.card-desc {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin-bottom: var(--space-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.meta-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.dot-active {
  background: var(--color-success);
}

/* 箭头 */
.card-arrow {
  color: var(--color-text-muted);
  flex-shrink: 0;
  transition: transform var(--transition-base);
}

.agent-card:hover .card-arrow {
  transform: translateX(3px);
  color: var(--color-primary);
}

/* ===== 底部 ===== */
.home-footer {
  margin-top: auto;
  padding: var(--space-8) 0 var(--space-4);
  font-size: var(--text-xs);
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.footer-dot {
  color: var(--color-border);
}

/* ===== 响应式 ===== */
@media (max-width: 640px) {
  .home {
    padding: var(--space-8) var(--space-4);
  }

  .home-header {
    margin-bottom: var(--space-8);
  }

  .brand-text h1 {
    font-size: var(--text-xl);
  }

  .agent-card {
    padding: var(--space-4);
  }

  .card-icon {
    width: 38px;
    height: 38px;
  }

  .card-icon svg {
    width: 20px;
    height: 20px;
  }
}
</style>
