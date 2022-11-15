/*
 * description
 */
package com.kkb.common.util;


import com.kkb.common.core.exception.KkbWebStatus;

/**
 * 类描述
 *
 * @author sy
 * @date 2021/9/9 11:52 上午
 * @since
 */
public enum CommonError implements KkbWebStatus {
    NO_PERMISSION(1, "权限认证失败"),
    PARAMETER_ERROR(2, "参数错误"),
    SAVE_ERROR(3, "保存失败"),
    UPDATE_ERROR(4, "更新失败"),
    CANCEL_ERROR(5, "取消失败"),
    SEARCH_ERROR(6, "查询失败"),
    DELETE_ERROR(7, "删除失败"),
    DATE_FORMAT_PATTERN_ERROR(8, "时间格式错误"),
    LOGIN_USER_INFO_ERROR(9, "登录信息丢失"),

    EXECUTE_ERROR(10000, "执行失败"),
    ;

    private int code;
    private String msg;

    CommonError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
