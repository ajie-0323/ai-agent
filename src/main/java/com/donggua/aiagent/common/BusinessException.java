package com.donggua.aiagent.common;

import lombok.Getter;

/**
 * 业务异常
 * 用于主动抛出的可预知业务异常，会被 GlobalExceptionHandler 统一拦截
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 使用错误码枚举构造
     */
    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用错误码枚举 + 自定义提示信息构造
     */
    public BusinessException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 使用自定义 code + message 构造
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 快捷构造——业务异常
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.BUSINESS_ERROR.getCode();
    }

    /**
     * 带 cause 的构造
     */
    public BusinessException(IErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
}
