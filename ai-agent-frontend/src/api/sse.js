export async function fetchSSE(url, { onMessage, onError, onComplete, signal }) {
  try {
    const response = await fetch(url, { signal });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = "";

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      buffer += decoder.decode(value, { stream: true });

      const parts = buffer.split("\n\n");
      buffer = parts.pop();

      for (const part of parts) {
        const lines = part.split("\n");
        for (const line of lines) {
          if (line.startsWith("data:")) {
            const data = line.slice(5).trim();
            if (data) {
              onMessage(data);
            }
          }
        }
      }
    }

    if (buffer.trim()) {
      const lines = buffer.trim().split("\n");
      for (const line of lines) {
        if (line.startsWith("data:")) {
          const data = line.slice(5).trim();
          if (data) {
            onMessage(data);
          }
        }
      }
    }

    onComplete?.();
  } catch (err) {
    if (err.name === "AbortError") {
      onComplete?.();
      return;
    }
    onError?.(err);
  }
}