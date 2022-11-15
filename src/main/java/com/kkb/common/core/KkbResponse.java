package com.kkb.common.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kkb.common.core.exception.KkbStatus;
import com.kkb.common.core.exception.KkbWebStatus;

import java.io.Serializable;

/**
 * @author kkb
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KkbResponse<T> implements Serializable {


    private static final long serialVersionUID = -7318467237446066728L;
    private int code;
    private String msg;
    /**
     * @JsonInclude(Include.NON_NULL) 这个注解放在类头上就可以解决。 实体类与json互转的时候 属性值为null的不参与序列化
     */
    private T data;

    public KkbResponse() {
        this(KkbStatus.SUCCESS, null);
    }

    public KkbResponse(T data) {
        this(KkbStatus.SUCCESS, data);
    }

    public KkbResponse(KkbWebStatus kkbWebStatus) {
        this(kkbWebStatus, null);
    }

    public KkbResponse(KkbWebStatus kkbWebStatus, T data) {
        this.code = kkbWebStatus.getCode();
        this.msg = kkbWebStatus.getMsg();
        this.data = data;
    }

    public KkbResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public KkbResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == KkbStatus.SUCCESS.getCode();
    }

    public static <T> KkbResponse<T> of(int code, String msg) {
        return new KkbResponse<>(code, msg);
    }

    public static <T> KkbResponse<T> of(int code, String msg, T data) {
        return new KkbResponse<>(code, msg, data);
    }

    public static <T> KkbResponse<T> success() {
        return new KkbResponse<>(KkbStatus.SUCCESS);
    }

    public static <T> KkbResponse<T> success(T data) {
        return new KkbResponse<>(KkbStatus.SUCCESS, data);
    }

    public static <T> KkbResponse<T> error() {
        return new KkbResponse<>(KkbStatus.FAILURE);
    }

    public static <T> KkbResponse<T> failure() {
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(), KkbStatus.FAILURE.getMsg());
    }

    public static <T> KkbResponse<T> failure(String message) {
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(), message);
    }

    @Override
    public String toString() {
        return "KkbResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + (data == null) +
                '}';
    }
}
