package com.kkb.common.send.ding.dto.phone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Objects;

/**
 * @author chixin
 * @version 1.0
 * @date 2020/6/17 8:07 下午
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpmsUser {

    /**
     * 钉钉UID信息
     */
    private String uid;

    /**
     * 用户名称
     */
    private String name;
    private String status;

    /**
     * 电话信息
     */
    private String phone;

    /**
     * 邮件
     */
    private String email;
    private String jobNumber;
    private String avatar;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        UpmsUser upmsUser = (UpmsUser) o;
        return Objects.equals(uid, upmsUser.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
