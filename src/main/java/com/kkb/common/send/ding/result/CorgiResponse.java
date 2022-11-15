/*
 * description
 */
package com.kkb.common.send.ding.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * cogi 接口返回
 *
 * @author ztkool
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CorgiResponse<T> implements Serializable {

    protected final static int SUCCESS_CODE = 1;

    protected int code;
    protected String msg;
    protected T data;

    public final boolean isSuccess() {
        return code == SUCCESS_CODE;
    }
}
