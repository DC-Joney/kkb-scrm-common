package com.kkb.common.core.exception;

import com.google.common.collect.Maps;
import com.kkb.common.core.KkbResponse;

import java.util.HashMap;

/**
 * KkbBaseStatus class description
 *
 * @author lbzheng@kaikeba.com
 * @date 2019-03-21
 */
public enum KkbStatus implements KkbWebStatus{
    /**
     * 成功
     */
    SUCCESS(0, "Success"),
    /**
     * 失败
     */
    FAILURE(1, "Failure"),
    /**
     * 数据不存在
     */
    NO_DATA(10001, "数据不存在"),
    /**
     * 数据已存在
     */
    DATA_EXIST(10002, "数据已存在"),
    /**
     * 验证码无效
     */
    INVALID_VERIFY_CODE(10003, "验证码无效"),
    /**
     * 认证授权失败
     */
    AUTHORIZATION_FAILED(10004, "认证授权失败"),
    /**
     * 每个设备绑定的营销号有数量限制，最多支持2个
     */
    MARKETING_ACCOUNT_LIMIT(10005, "每个设备绑定的营销号有数量限制，最多支持2个"),
    /**
     * 手机号已存在
     */
    MOBILE_EXIST(10006, "手机号已存在"),
    /**
     * 邮箱已存在
     */
    EMAIL_EXIST(10007, "邮箱已存在"),
    /**
     * 邮箱格式不正确
     */
    INVALID_EMAIL(10008, "邮箱格式不正确"),
    /**
     * 手机号格式不正确
     */
    INVALID_MOBILE(10009, "手机号格式不正确"),
    /**
     * 未获取到学习中心信息
     */
    NO_LEARN_DATA(10010, "未获取到学习中心信息"),
    /**
     * 未获取到upms信息
     *
     * @author zyz
     */
    NO_UPMS_DATA(10110, "未获取到upms信息"),
    /**
     * upms权限异常
     *
     * @author zyz
     */
    UPMS_AUTH_ERROR(10111, "upms权限异常"),
    /**
     * upms权限异常
     *
     * @author zyz
     */
    UPMS_ERROR(10112, "upms程序异常"),

    /**
     * 未登录
     */
    NO_LOGIN(10011, "请先登录"),
    /**
     * 数据量过大，操作失败
     */
    TOO_MUCH_DATA(10011, "数据量过大，操作失败"),
    /**
     * 订单已退款
     */
    REFUND_ORDERS(10012, "订单已退款"),
    /**
     * 未知异常,请联系管理员!
     */
    UNKNOW_EXCEPTION(500, "未知异常,请联系管理员!"),
    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    /**
     * 用户名或密码错误
     */
    ACCOUNT_BAD_CREDENTIALS(401, "用户名或密码错误!"),
    /**
     * 未实名
     */
    CERTIFICATION_NOT_FOUND(920500003, "未找到实名信息"),
    /**
     * 未能查到相关的数据
     */
    EXCEPTION_CODE_404(1003, "无效的参数"),
    /**
     * 身份不正确
     */
    CERTIFICATION_INFO_INVALID(920500004, "身份信息错误"),
    /**
     * 相关数据已存在
     */
    EXCEPTION_CODE_1001(1001, "已存在"),
    /**
     * 正在处理中，请稍后！
     */
    FAILURE_LOADING(-1, "正在处理中，请稍后！"),
    /**
     * 认证授权失败
     */
    UNAUTHORIZED(10004, "认证授权失败"),
    /**
     * 无权限访问
     */
    NO_ACCESS(10005, "无权限访问"),

    /**
     * 该帐号已被禁用，请联系系统管理员!
     */
    ACCOUNT_LOCK(402, "该帐号已被禁用，请联系系统管理员!"),
    /**
     * 不允许访问
     */
    ACCESS_DENIED(403, "不允许访问"),

    ES_TOO_LARGE(3000, "分页查询太大,es不支持"),

    ORDER_REPEAT(30001, "已购买该课程,请勿重复下单."),

    REQUEST_FREQUENTLY(4000, "频繁请求"),

    NOT_CHANGE_CLASS(5001, "没有可切换的班次"),

    NOT_GROUP_SELL(5002, "当前班次不是接量组分量"),

    NOT_CONFIG_GROUP(5003, "当前班次未配置接量组"),

    NOT_WORKING_CLASS(5004, "没有招生中的班次"),

    FEIGN_FALLBACK_EXCEPTION(900000, "feign调用异常"),
    FEIGN_WEB_SOCKET_EXCEPTION(900001, "webSocket调用异常"),
    ;


    private int code;
    private String msg;

    KkbStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    private static HashMap<Integer, KkbStatus> map = Maps.newHashMap();

    static {
        for (KkbStatus d : KkbStatus.values()) {
            map.put(d.code, d);
        }
    }

    public static KkbStatus parse(int code) {
        if (map.containsKey(code)) {
            return map.get(code);
        }
        return null;
    }

    public static boolean isSuccess(KkbResponse response) {
        return SUCCESS.getCode() == response.getCode();
    }

    public boolean equals(int code){
        return this.getCode() == code;
    }

}
