package com.donggua.aiagent.common;

/**
 * 通用错误码枚举
 */
public enum ErrorCode implements IErrorCode {

    // ==================== 通用 ====================
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    // ==================== 客户端错误 4xx ====================
    BAD_REQUEST(400, "请求参数有误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    PARAM_VALIDATE_FAILED(422, "参数校验失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // ==================== 服务端错误 5xx ====================
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // ==================== 业务错误 ====================
    BUSINESS_ERROR(1000, "业务异常"),
    DATA_NOT_FOUND(1001, "数据不存在"),
    DATA_EXISTED(1002, "数据已存在"),
    OPERATION_FAILED(1003, "操作失败"),
    ILLEGAL_STATE(1004, "非法状态"),
    ILLEGAL_ARGUMENT(1005, "非法参数"),
    FILE_TOO_LARGE(1006, "文件过大"),
    FILE_TYPE_NOT_SUPPORTED(1007, "不支持的文件类型"),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
