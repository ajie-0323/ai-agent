import axios from "axios";

const request = axios.create({
  baseURL: "/api",
  timeout: 30000,
});

// 响应拦截器：自动解包 ResponseResult
request.interceptors.response.use(
  (response) => {
    const res = response.data;

    // 如果后端返回了统一格式（有 code 和 success 字段），自动判断
    if (res.code !== undefined && res.success !== undefined) {
      if (res.success) {
        // 只返回真正的业务数据，外部不用再 .data.data
        return res.data;
      }
      // 业务失败，统一抛错
      return Promise.reject(new Error(res.message || "请求失败"));
    }

    // 非标准格式（如流式、文件下载等），直接返回
    return res;
  },
  (error) => {
    // 网络错误 / HTTP 状态码错误统一处理
    let message = "网络异常，请稍后重试";
    if (error.response) {
      const status = error.response.status;
      if (status === 401) message = "未授权，请重新登录";
      else if (status === 403) message = "权限不足";
      else if (status === 404) message = "请求的资源不存在";
      else if (status >= 500) message = "服务器内部错误，请稍后重试";
    } else if (error.code === "ECONNABORTED") {
      message = "请求超时，请稍后重试";
    }
    console.error("请求异常:", message, error);
    return Promise.reject(new Error(message));
  }
);

export default request;
