/**
 * 打字机效果工具函数
 *
 * 接收 SSE 流式数据放入缓冲区，逐字写入 ref（默认 25ms/字）
 * 流完成后立即刷新剩余内容
 *
 * @param {import("vue").Ref<string>} displayTextRef - 要显示内容的 ref
 * @param {number} speed - 每字间隔（毫秒）
 */
export function createTypewriter(displayTextRef, speed = 25) {
  let buffer = "";
  let timerId = null;
  let completed = false;

  function consume() {
    timerId = setInterval(() => {
      if (buffer.length > 0) {
        displayTextRef.value += buffer[0];
        buffer = buffer.slice(1);
      } else if (completed) {
        clearInterval(timerId);
        timerId = null;
      }
    }, speed);
  }

  /**
   * 向缓冲区追加文本。如尚未开始消费则启动定时器
   */
  function feed(text) {
    buffer += text;
    if (!timerId && !completed) {
      consume();
    }
  }

  /**
   * 标记流完成，立即刷完缓冲区
   */
  function complete() {
    completed = true;
    if (buffer.length > 0) {
      displayTextRef.value += buffer;
      buffer = "";
    }
    if (timerId) {
      clearInterval(timerId);
      timerId = null;
    }
  }

  /**
   * 中断打字机，停止消费
   */
  function cancel() {
    if (timerId) {
      clearInterval(timerId);
      timerId = null;
    }
    displayTextRef.value += buffer;
    buffer = "";
    completed = true;
  }

  return { feed, complete, cancel };
}