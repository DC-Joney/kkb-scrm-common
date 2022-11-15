package com.kkb.common.mybatis.base.mybatise;

import java.io.Serializable;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：Model
 * 类描述：基类Model
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:06:26
 * version 2.0
 */
public interface Model<PK> extends Serializable {

    PK getId();

    void setId(PK id);
}