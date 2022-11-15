package com.kkb.common.mybatis.base.mybatise;



import com.kkb.common.mybatis.base.mybatise.model.Order;
import com.kkb.common.mybatis.base.mybatise.model.PageInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：Query
 * 类描述：查询构造器
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:06:53
 * version 2.0
 */
public interface Query <T extends Serializable> {

    Query<T> filterModel(Object model)throws Exception ;

    Query<T> addFilterProperty(String propertyName, Object val);

    Query<T> addFilterMap(Map<String, Object> propertyValMap);

    Long count()throws Exception;

    T singleResult();

    List<T> list();

    List<T> list(String... columns);

    <E> List<E> listColumn(String column);

    PageInfo<T> listPage(int page, int pageSize, String... columns) throws Exception;

    Query<T> order(String column, Order order);

    Query<T> condition(String condition);
}
