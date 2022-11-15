package com.kkb.common.mybatis.base.mybatise;

import com.kkb.common.mybatis.base.mybatise.model.PageInfo;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：DefaultDao
 * 类描述：默认Dao
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:05:50
 * version 2.0
 */
public class DefaultDAO<T extends Model<PK>, PK extends Object> extends SqlSessionDaoSupport implements DAO<T, PK> {

    private static final Integer DEFAULT_BATCH_SIZE = 100;

    private String mapperNameSpace = this.getClass().getName();

    @Override
    public T getById(PK id) {
        return this.getSqlSession().selectOne(this.mapperNameSpace + ".getById", id);
    }

    @Override
    public T findOneByEntity(T model) {
        return this.getSqlSession().selectOne(this.mapperNameSpace + ".findOneByEntity", model);
    }

    @Override
    public List<T> findByEntity(T model) {
        return this.getSqlSession().selectList(this.mapperNameSpace + ".findByEntity", model);
    }

    @Override
    public int insert(T model) {
        return this.getSqlSession().insert(this.mapperNameSpace + ".insert", model);
    }

    @Override
    public void insertAndGetId(T model) {
        this.getSqlSession().insert(this.mapperNameSpace + ".insertAndGetId", model);
    }

    @Override
    public int batchInsert(List<T> models) {
        return batchInsert(models, DefaultDAO.DEFAULT_BATCH_SIZE);
    }

    @Override
    public int batchInsert(List<T> models, int size) {
        int total = 0;
        int j = 0;
        List<T> temp = new ArrayList<>();
        for (T t : models) {
            ++j;
            if (j >= size) {
                temp.add(t);
                total += this.getSqlSession().insert(this.mapperNameSpace + ".batchInsert", temp);
                temp = new ArrayList<>();
                j = 0;
            } else {
                temp.add(t);
            }
        }
        if (temp.size() > 0) {
            total += this.getSqlSession().insert(this.mapperNameSpace + ".batchInsert", temp);
        }
        return total;
    }

    @Override
    public int update(T model) {
        return this.getSqlSession().update(this.mapperNameSpace + ".update", model);
    }

    @Override
    public int updateNotNull(T model) {
        return this.getSqlSession().update(this.mapperNameSpace + ".updateNotNull", model);
    }

    @Override
    public int deleteById(PK id) {
        return this.getSqlSession().delete(this.mapperNameSpace + ".deleteById", id);
    }

    @Override
    public int deleteByIds(PK[] ids) {
        return ids != null && ids.length != 0 ? this.getSqlSession().delete(this.mapperNameSpace + ".deleteIds", ids) : 0;
    }

    @Override
    public int deleteEntity(T model) throws Exception {
        if (ObjectUtils.isObjectEmpty(model)) {
            throw new RuntimeException("deleteByEntity：model can not be an empty object ");
        } else {
            return this.getSqlSession().delete(this.mapperNameSpace + ".deleteByColumn", model);
        }
    }

    @Override
    public int deleteByColumn(Enum col, Object value) {
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException("deleteByColumn：value not not be null");
        } else {
            Map<String, Object> param = new HashMap<>();
            param.put(col.name(), value);
            return this.getSqlSession().delete(this.mapperNameSpace + ".deleteByColumn", param);
        }
    }

    @Override
    public List<T> listColumn(Map map) {
        return this.getSqlSession().selectList(this.mapperNameSpace + ".listColumn", map);
    }

    @Override
    public List<T> page(PageInfo pageInfo) {
        String statement = this.mapperNameSpace  + ".page";
        int pageIndex = (pageInfo.getPage() - 1) * pageInfo.getPageSize();
        int pageSize = pageInfo.getPageSize();
        RowBounds rowBounds = new RowBounds(pageIndex, pageSize);
        return this.getSqlSession().selectList(statement, pageInfo.getParams(), rowBounds);
    }

    @Override
    public Long pageTotalCount(Map<String, Object> params,String sqlName) throws Exception {
        Long result = 0L;
        String statement = this.mapperNameSpace  + sqlName;
        MappedStatement mappedStatement = this.getSqlSession().getConfiguration().getMappedStatement(statement);
        BoundSql boundSql = mappedStatement.getBoundSql(params);
        DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement, params, boundSql);
        Connection connection;
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        SqlSessionTemplate temp = null;
        DefaultSqlSession defaultSession = null;
        try {
            temp = (SqlSessionTemplate)this.getSqlSession();
            defaultSession = (DefaultSqlSession) SqlSessionUtils.getSqlSession(temp.getSqlSessionFactory(), temp.getExecutorType(), temp.getPersistenceExceptionTranslator());
            connection = defaultSession.getConnection();
            String countSql = "";
            if(boundSql.getSql().contains("GROUP BY")){
                countSql = "SELECT COUNT(*)" + boundSql.getSql().substring(boundSql.getSql().indexOf(" FROM "),boundSql.getSql().indexOf(" GROUP "));
            }else{
                countSql = "SELECT COUNT(*)" + boundSql.getSql().substring(boundSql.getSql().indexOf(" FROM "));
            }
            countStmt = connection.prepareStatement(countSql);
            defaultParameterHandler.setParameters(countStmt);
            rs = countStmt.executeQuery();
            if (rs.next()) {
                result = rs.getLong(1);
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (countStmt != null) {
                    countStmt.close();
                }
                if (defaultSession != null) {
                    SqlSessionUtils.closeSqlSession(defaultSession, temp.getSqlSessionFactory());
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        return result;
    }

    @Override
    public Query<T> createQuery() {
        return new DefaultQuery<>(this, this.mapperNameSpace + ".list");
    }

    @Override
    public Query<T> createTQuery(String statement) {
        return new DefaultQuery(this, statement);
    }

    @Override
    public Query<?> createObjectQuery(String statement) {
        return new DefaultQuery(this, statement);
    }

    @Override
    public List<T> list() {
        return this.createQuery().list();
    }

    @Override
    public List<T> listByIds(List<PK> ids) {
        return ids != null && ids.size() != 0 ? this.createQuery().addFilterProperty("list_ids", ids).list() : new ArrayList();
    }

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
}