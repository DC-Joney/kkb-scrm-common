package com.kkb.common.mybatis.base.mybatise;


import com.kkb.common.mybatis.base.mybatise.model.PageInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：DAO
 * 类描述：DAO
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:05:34
 * version 2.0
 */
public interface DAO<T extends Serializable, PK extends Object> {

    T getById(PK id);

    T findOneByEntity(T model);

    List<T> findByEntity(T model);

    int insert(T model);

    void insertAndGetId(T model);

    int batchInsert(List<T> models);

    int batchInsert(List<T> models, int size);

    int update(T model);

    int updateNotNull(T model);

    int deleteById(PK id);

    int deleteByIds(PK[] ids);

    int deleteByColumn(Enum col, Object val);

    int deleteEntity(T model) throws Exception;

    Query<T> createQuery();

    Query<T> createTQuery(String statement);

    Query<?> createObjectQuery(String statement);

    List<T> list();

    List<T> listByIds(List<PK> ids);

    List<T> listColumn(Map map);

    List<T> page(PageInfo pageInfo);

    Long pageTotalCount(Map<String, Object> params, String sqlName) throws Exception;

}
