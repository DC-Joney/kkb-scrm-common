package com.kkb.common.tools.concurrent.record;

/**
 * 找不到 对应的 request 异常
 * @author zhangyang
 */
public class RequestNotFoundException extends RuntimeException{

    public RequestNotFoundException(String message) {
        super(message);
    }

    public RequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
