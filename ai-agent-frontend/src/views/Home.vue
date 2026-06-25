<template>
  <div class="home">
    <header class="home-header">
      <h1>AI Agent 平台</h1>
      <p>选择一个应用开始体验</p>
    </header>
    <div class="app-grid">
      <div class="app-card" @click="$router.push('/love-chat')">
        <div class="app-icon">💕</div>
        <h2>AI 恋爱大师</h2>
        <p>智能分析情感问题，提供恋爱建议</p>
        <span class="app-tag">实时对话</span>
      </div>
      <div class="app-card" @click="$router.push('/manus-chat')">
        <div class="app-icon">🧠</div>
        <h2>AI 超级智能体</h2>
        <p>通用 AI 助手，帮你完成各种任务</p>
        <span class="app-tag">实时对话</span>
      </div>
    </div>
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
  justify-content: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  background: var(--color-gradient);
}

.home-header {
  text-align: center;
  margin-bottom: 60px;
  color: var(--color-white);
}

.home-header h1 {
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-md);
  letter-spacing: 2px;
}

.home-header p {
  font-size: var(--font-size-lg);
  opacity: 0.85;
}

.app-grid {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;
}

.app-card {
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl) 28px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: var(--shadow-lg);
}

.app-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.25);
}

.app-icon {
  font-size: 64px;
  margin-bottom: var(--spacing-lg);
  line-height: 1;
}

.app-card h2 {
  font-size: var(--font-size-xl);
  color: var(--color-text);
  margin-bottom: var(--spacing-sm);
}

.app-card p {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
}

.app-tag {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  background: var(--color-gradient);
  color: var(--color-white);
  font-size: var(--font-size-xs);
}

/* ===== 响应式 ===== */

@media (max-width: 767px) {
  .home {
    padding: var(--spacing-xl) var(--spacing-md);
    justify-content: flex-start;
    padding-top: 80px;
  }

  .home-header {
    margin-bottom: 40px;
  }

  .home-header h1 {
    font-size: 28px;
  }

  .home-header p {
    font-size: var(--font-size-md);
  }

  .app-grid {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  .app-card {
    width: 100%;
    max-width: 340px;
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .app-icon {
    font-size: 48px;
  }
}

@media (min-width: 768px) and (max-width: 1024px) {
  .app-card {
    width: 280px;
    padding: var(--spacing-xl) var(--spacing-lg);
  }
}
</style>