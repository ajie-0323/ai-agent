<template>
  <div class="home">
    <!-- 浮动装饰圆 -->
    <div class="floating-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>

    <header class="home-header">
      <div class="logo-wrapper">
        <div class="logo">🤖</div>
      </div>
      <h1 class="title">AI Agent 平台</h1>
      <p class="subtitle">选择一个应用开始体验</p>
    </header>

    <div class="app-grid">
      <div class="app-card" @click="$router.push('/love-chat')">
        <div class="card-glow"></div>
        <div class="app-icon">💕</div>
        <h2>AI 恋爱大师</h2>
        <p>智能分析情感问题，提供恋爱建议</p>
        <div class="card-footer">
          <span class="app-tag">实时对话</span>
          <span class="app-tag tag-arrow">进入 →</span>
        </div>
      </div>

      <div class="app-card" @click="$router.push('/manus-chat')">
        <div class="card-glow"></div>
        <div class="app-icon">🧠</div>
        <h2>AI 超级智能体</h2>
        <p>通用 AI 助手，帮你完成各种任务</p>
        <div class="card-footer">
          <span class="app-tag">实时对话</span>
          <span class="app-tag tag-arrow">进入 →</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
/* ===== 页面容器 ===== */
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 300% 300%;
  animation: gradientMove 8s ease infinite;
  position: relative;
  overflow: hidden;
}

@keyframes gradientMove {
  0%   { background-position: 0% 50%; }
  50%  { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* ===== 浮动装饰形状 ===== */
.floating-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
  animation: floatUp 12s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.3);
  top: -80px;
  left: -60px;
  animation-duration: 14s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.25);
  bottom: 10%;
  right: -40px;
  animation-duration: 10s;
  animation-delay: -3s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.2);
  top: 50%;
  left: 8%;
  animation-duration: 16s;
  animation-delay: -6s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.18);
  bottom: 15%;
  left: 30%;
  animation-duration: 11s;
  animation-delay: -2s;
}

@keyframes floatUp {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-30px) scale(1.05);
  }
}

/* ===== 头部 ===== */
.home-header {
  text-align: center;
  margin-bottom: 60px;
  color: var(--color-white);
  position: relative;
  z-index: 1;
  animation: fadeInDown 0.8s ease both;
}

.logo-wrapper {
  margin-bottom: 20px;
}

.logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  font-size: 42px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 24px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  animation: pulseGlow 3s ease-in-out infinite;
}

@keyframes pulseGlow {
  0%, 100% {
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  50% {
    box-shadow: 0 8px 48px rgba(255, 255, 255, 0.25);
  }
}

.title {
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-md);
  letter-spacing: 3px;
  font-weight: 800;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.subtitle {
  font-size: var(--font-size-lg);
  opacity: 0.85;
  font-weight: 300;
  letter-spacing: 1px;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 卡片网格 ===== */
.app-grid {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;
  position: relative;
  z-index: 1;
}

/* ===== 卡片 ===== */
.app-card {
  width: 300px;
  background: rgba(255, 255, 255, 0.93);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl) 28px 28px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
              box-shadow 0.4s ease;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  animation: cardFadeIn 0.6s ease both;
}

.app-card:nth-child(1) { animation-delay: 0.2s; }
.app-card:nth-child(2) { animation-delay: 0.4s; }

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 卡片光晕 hover 效果 */
.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
    rgba(102, 126, 234, 0.08) 0%,
    transparent 60%
  );
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
}

.app-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.app-card:hover .card-glow {
  opacity: 1;
}

.app-card:active {
  transform: translateY(-4px) scale(0.98);
}

/* ===== 卡片内容 ===== */
.app-icon {
  font-size: 58px;
  margin-bottom: var(--spacing-lg);
  line-height: 1;
  display: inline-block;
  transition: transform 0.3s ease;
}

.app-card:hover .app-icon {
  transform: scale(1.15) rotate(-5deg);
}

.app-card h2 {
  font-size: var(--font-size-xl);
  color: var(--color-text);
  margin-bottom: var(--spacing-sm);
  font-weight: 700;
}

.app-card p {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.7;
  margin-bottom: 0;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: var(--spacing-lg);
}

.app-tag {
  display: inline-block;
  padding: 6px 16px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: var(--color-white);
  font-size: var(--font-size-xs);
  font-weight: 500;
  letter-spacing: 0.5px;
}

.tag-arrow {
  background: rgba(102, 126, 234, 0.12);
  color: var(--color-primary);
  font-weight: 600;
  transition: all 0.3s ease;
}

.app-card:hover .tag-arrow {
  background: var(--color-gradient);
  color: var(--color-white);
  transform: translateX(4px);
}

/* ===== 响应式 ===== */

@media (max-width: 767px) {
  .home {
    padding: var(--spacing-xl) var(--spacing-md);
    justify-content: flex-start;
    padding-top: 60px;
  }

  .home-header {
    margin-bottom: 40px;
  }

  .title {
    font-size: 30px;
    letter-spacing: 2px;
  }

  .subtitle {
    font-size: var(--font-size-md);
  }

  .logo {
    width: 64px;
    height: 64px;
    font-size: 34px;
    border-radius: 18px;
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
    font-size: 46px;
  }

  .shape-1 { width: 180px; height: 180px; top: -40px; left: -40px; }
  .shape-2 { width: 120px; height: 120px; }
  .shape-3 { width: 80px; height: 80px; }
  .shape-4 { width: 60px; height: 60px; }
}

@media (min-width: 768px) and (max-width: 1024px) {
  .app-card {
    width: 280px;
    padding: var(--spacing-xl) var(--spacing-lg);
  }
}
</style>
