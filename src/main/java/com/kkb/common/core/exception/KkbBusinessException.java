/*
 * description
 */
package com.kkb.common.core.exception;

import cn.hutool.core.util.StrUtil;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.Getter;

/**
 * 业务异常，主要用于全局异常handler处理
 * 注意，如果是服务间调用，则不能抛出此异常，会触发熔断
 * 想避免触发熔断，需要在FeignClient注解中配置Configuration
 */

/**
 * 已将RuntimeException改成HystrixBadRequestException
 * HystrixBadRequestException不会触发熔断
 * 适用于业务的异常，如参数缺失，数据重复等业务判断异常
 */
@Getter
public class KkbBusinessException extends HystrixBadRequestException {

    private int code;
    private String msg;
    private Throwable error;
    private Object data;

    public KkbBusinessException withCode(int code) {
        this.code = code;
        return this;
    }

    public KkbBusinessException withMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public KkbBusinessException withMsg(String pattern, Object... args) {
        this.msg = StrUtil.format(pattern, args);
        return this;
    }

    public KkbBusinessException withData(Object data) {
        this.data = data;
        return this;
    }

    public KkbBusinessException withError(Throwable error) {
        this.error = error;
        return this;
    }

    private KkbBusinessException(int code, String msg, Object data, Throwable error) {
        super(msg, error);
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.error = error;
    }

    private KkbBusinessException(KkbWebStatus kkbWebStatus) {
        super(kkbWebStatus.getMsg());
        this.code = kkbWebStatus.getCode();
        this.msg = kkbWebStatus.getMsg();
    }

    public static KkbBusinessException of(int code, String msg, Object data, Throwable error) {
        return new KkbBusinessException(code, msg, data, error);
    }

    public static KkbBusinessException of() {
        return new KkbBusinessException(KkbStatus.FAILURE.getCode(), "This is blank message", null, null);
    }

    public static KkbBusinessException of(String msg) {
        return new KkbBusinessException(KkbStatus.FAILURE.getCode(), msg, null, null);
    }

    public static KkbBusinessException of(String pattern, Object... args) {
        return new KkbBusinessException(KkbStatus.FAILURE.getCode(), StrUtil.format(pattern, args), null, null);
    }

    public static KkbBusinessException of(int code, String msg) {
        return of(code, msg, null, null);
    }

    public static KkbBusinessException of(int code, String msg, Throwable error) {
        return of(code, msg, null, error);
    }

    public static KkbBusinessException of(int code, String msg, Object data) {
        return of(code, msg, data, null);
    }

    public static KkbBusinessException of(KkbWebStatus kkbWebStatus) {
        if (null == kkbWebStatus) {
            throw new IllegalArgumentException("exceptionEnum is null.");
        }
        return new KkbBusinessException(kkbWebStatus);
    }

}
