package com.kkb.common.mybatis.base.service;



import com.kkb.common.mybatis.base.log.Logger;
import com.kkb.common.mybatis.base.mybatise.Model;
import com.kkb.common.mybatis.base.mybatise.model.PageInfo;
import com.kkb.common.mybatis.base.log.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：AbstractBaseService
 * 类描述：service抽象类
 * 创建人：yuangl
 * 创建时间：2017-12-12 23:16
 * version 2.0
 */
public abstract class AbstractBaseService<T extends Model<PK>, PK extends Object> implements BaseService<T, PK>{
    
    private static Logger logger = LoggerFactory.getLogger(AbstractBaseService.class);

    @Override
    public void save(T model) {
        if (model.getId() == null) {
            this.getDao().insert(model);
        } else {
            this.getDao().updateNotNull(model);
        }
    }

    @Override
    public void insert(T model) {
        this.getDao().insert(model);
    }

    @Override
    public void update(T model) {
        this.getDao().updateNotNull(model);
    }

    @Override
    public void deleteById(PK id) {
        this.getDao().deleteById(id);
    }

    @Override
    public T getById(PK id) {
        return this.getDao().getById(id);
    }

    @Override
    public T queryColumnById(PK id, String... columns) {
        List<T> list = this.getDao().createQuery().addFilterProperty("id", id).list(columns);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<T> listAll() {
        return this.getDao().list();
    }

    @Override
    public List<T> listByIds(List<PK> ids) {
        return (ids != null && ids.size() != 0 ? this.getDao().listByIds(ids) : new ArrayList());
    }

    @Override
    public List<T> listByEntity(T model)throws Exception {
        return this.getDao().createQuery().filterModel(model).list();
    }

    @Override
    public boolean checkId(PK id)throws Exception {
        return this.getDao().createQuery().addFilterProperty("id", id).count() > 0;
    }

    @Override
    public boolean check(Enum column, Object val)throws Exception {
        return this.getDao().createQuery().addFilterProperty(column.name(), val).count() > 0;
    }

    @Override
    public boolean check(Enum column, Object val, PK id)throws Exception {
        if (id==null) {
            return this.check(column, val);
        } else if (this.getDao().createQuery().addFilterProperty(column.name(), val).count() > 0) {
            T t = this.getDao().createQuery().addFilterProperty(column.name(), val).singleResult();
            return !(t.getId().equals(id));
        } else {
            return false;
        }
    }


    @Override
    public PageInfo page(PageInfo<T> pageInfo)throws Exception {
        pageInfo.setData(this.getDao().page(pageInfo));
        pageInfo.setRecordsTotal(this.getDao().pageTotalCount(pageInfo.getParams(),".page"));
        return pageInfo;
    }

}
