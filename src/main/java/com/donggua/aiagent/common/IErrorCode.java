package com.donggua.aiagent.common;

/**
 * 错误码接口
 * 所有错误码枚举都应实现此接口
 */
public interface IErrorCode {

    /** 获取状态码 */
    int getCode();

    /** 获取提示信息 */
    String getMessage();
}
