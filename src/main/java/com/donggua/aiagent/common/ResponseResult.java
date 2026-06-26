package com.donggua.aiagent.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应结果封装
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 是否成功 */
    private boolean success;

    // ==================== 成功 ====================

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null, true);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data, true);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(ErrorCode.SUCCESS.getCode(), message, data, true);
    }

    // ==================== 失败 ====================

    public static <T> ResponseResult<T> error() {
        return new ResponseResult<>(ErrorCode.FAILED.getCode(), ErrorCode.FAILED.getMessage(), null, false);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(ErrorCode.FAILED.getCode(), message, null, false);
    }

    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<>(code, message, null, false);
    }

    public static <T> ResponseResult<T> error(IErrorCode errorCode) {
        return new ResponseResult<>(errorCode.getCode(), errorCode.getMessage(), null, false);
    }

    public static <T> ResponseResult<T> error(IErrorCode errorCode, String message) {
        return new ResponseResult<>(errorCode.getCode(), message, null, false);
    }

    // ==================== 便捷判断 ====================

    public boolean isSuccess() {
        return this.success;
    }
}
