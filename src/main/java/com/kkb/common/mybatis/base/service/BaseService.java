package com.kkb.common.mybatis.base.service;



import com.kkb.common.mybatis.base.mybatise.DAO;
import com.kkb.common.mybatis.base.mybatise.model.PageInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：BaseService
 * 类描述：Service
 * 创建人：yuangl
 * 创建时间：2017-12-12 23:06
 * version 2.0
 */
public interface BaseService<T extends Serializable, PK extends Object> {

    DAO<T, PK> getDao();

    void save(T model);

    void insert(T model);

    void update(T model);

    void deleteById(PK id);

    T getById(PK id);

    T queryColumnById(PK id, String... columns);

    List<T> listAll();

    List<T> listByIds(List<PK> ids);

    List<T> listByEntity(T model)throws Exception;

    boolean checkId(PK id)throws Exception;

    boolean check(Enum column, Object val)throws Exception;

    boolean check(Enum column, Object val, PK id)throws Exception;

    PageInfo page(PageInfo<T> pageInfo)throws Exception;
}
