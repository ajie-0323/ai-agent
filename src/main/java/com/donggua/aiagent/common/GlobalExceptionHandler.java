package com.donggua.aiagent.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一拦截所有 Controller 抛出的异常，返回标准的 ResponseResult
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    @ExceptionHandler(BusinessException.class)
    public ResponseResult<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseResult.error(e.getCode(), e.getMessage());
    }

    // ==================== 参数校验异常 ====================

    /**
     * 处理 @RequestBody 配合 @Validated 的参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ResponseResult.error(ErrorCode.PARAM_VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 处理 @RequestParam 上 @NotBlank / @NotNull 等校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数约束校验失败: {}", message);
        return ResponseResult.error(ErrorCode.PARAM_VALIDATE_FAILED.getCode(), message);
    }

    // ==================== 参数 / 请求相关异常 ====================

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseResult<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getMessage());
        return ResponseResult.error(ErrorCode.BAD_REQUEST.getCode(), "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseResult<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {}", e.getMessage());
        return ResponseResult.error(ErrorCode.BAD_REQUEST.getCode(), "参数 " + e.getName() + " 类型不匹配");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体不可读: {}", e.getMessage());
        return ResponseResult.error(ErrorCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    // ==================== HTTP 方法 / 媒体类型 ====================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return ResponseResult.error(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseResult<Void> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("不支持的媒体类型: {}", e.getMessage());
        return ResponseResult.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    // ==================== 资源不存在 ====================

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseResult<Void> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return ResponseResult.error(ErrorCode.NOT_FOUND.getCode(), "请求的资源不存在: " + e.getResourcePath());
    }

    // ==================== 兜底异常 ====================

    @ExceptionHandler(Exception.class)
    public ResponseResult<Void> handleException(Exception e) {
        log.error("未捕获异常: ", e);
        return ResponseResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "服务器内部错误，请稍后重试");
    }
}
