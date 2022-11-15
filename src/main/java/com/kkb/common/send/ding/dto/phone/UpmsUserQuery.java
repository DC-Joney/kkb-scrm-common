package com.kkb.common.send.ding.dto.phone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class UpmsUserQuery implements Serializable {

    /**
     * 电话
     */
    private String phone;


    /**
     * 名字
     */
    private String name;

    /**
     * 数据条数
     * <p>
     * 0 < limit <= 100
     */
    private Integer pageSize = 50;
    /**
     * 页码
     */
    private Integer pageNum = 1;

    public static UpmsUserQuery create(String phone){
        UpmsUserQuery userQuery = new UpmsUserQuery();
        userQuery.setPhone(phone);
        return userQuery;
    }


}