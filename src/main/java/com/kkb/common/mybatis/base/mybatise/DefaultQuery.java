package com.kkb.common.mybatis.base.mybatise;

import com.kkb.common.mybatis.base.mybatise.model.Order;
import com.kkb.common.mybatis.base.mybatise.model.PageInfo;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：DefaultQuery
 * 类描述：默认查询构造器
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:06:07
 * version 2.0
 */
public class DefaultQuery<T extends Model<?>> implements Query<T> {

    private static final Log logger = LogFactory.getLog(DefaultQuery.class);

    private Map<String, Object> filterMap;
    private DefaultDAO<T, ?> defaultDAO;
    private String listStatement;
    private StringBuffer sortColumns;
    private boolean hasSortColumn = false;
    private StringBuffer appendCondition;

    public DefaultQuery(DefaultDAO<T, ?> defaultDAO, String statement) {
        this.defaultDAO = defaultDAO;
        this.filterMap = new HashMap<>();
        this.listStatement = statement;
        this.sortColumns = new StringBuffer();
        this.appendCondition = new StringBuffer();
    }

    @Override
    public Query<T> filterModel(Object model)throws Exception {
        Map filter = PropertyUtils.describe(model);
        this.filterMap.putAll(filter);
        return this;
    }

    @Override
    public Query<T> addFilterProperty(String propertyName, Object val) {
        this.filterMap.put(propertyName, val);
        return this;
    }

    @Override
    public Query<T> addFilterMap(Map<String, Object> propertyValMap) {
        this.filterMap.putAll(propertyValMap);
        return this;
    }

    @Override
    public Long count()throws Exception {
        if (this.appendCondition.length() > 1) {
            this.filterMap.put("appendCondition", this.appendCondition);
        }

        return this.getCount(this.filterMap);
    }

    @Override
    public T singleResult() {
        if (this.appendCondition.length() > 1) {
            this.filterMap.put("appendCondition", this.appendCondition);
        }
        return this.defaultDAO.getSqlSession().selectOne(this.listStatement, this.filterMap);
    }

    @Override
    public List<T> list() {
        if (this.sortColumns.length() > 1) {
            this.filterMap.put("sortColumns", this.sortColumns);
        }

        if (this.appendCondition.length() > 1) {
            this.filterMap.put("appendCondition", this.appendCondition);
        }

        return this.defaultDAO.getSqlSession().selectList(this.listStatement, this.filterMap);
    }

    @Override
    public List<T> list(String... columns) {
        this.filterMap.put("LIST_BYCOLUMN", true);

        for (String column : columns) {
            this.filterMap.put(column + "_column", true);
        }

        if (this.sortColumns.length() > 1) {
            this.filterMap.put("sortColumns", this.sortColumns);
        }

        if (this.appendCondition.length() > 1) {
            this.filterMap.put("appendCondition", this.appendCondition);
        }

        List<T> resList = this.defaultDAO.getSqlSession().selectList(this.listStatement, this.filterMap);
        this.filterMap.remove("LIST_BYCOLUMN");
        for (String column : columns) {
            this.filterMap.remove(column + "_column");
        }

        return resList;
    }

    @Override
    public <E> List<E> listColumn(String column) {
        if (this.sortColumns.length() > 1) {
            this.filterMap.put("sortColumns", this.sortColumns);
        }

        if (this.appendCondition.length() > 1) {
            this.filterMap.put("appendCondition", this.appendCondition);
        }

        this.filterMap.put(column + "_column", true);
        List<E> list = this.defaultDAO.getSqlSession().selectList(this.listStatement + "Column", this.filterMap);
        this.filterMap.remove(column + "_column");
        return list;
    }

    @Override
    public PageInfo<T> listPage(int page, int pageSize, String... columns)throws Exception {
        PageInfo<T> pageInfo = new PageInfo<>();

        if(columns != null) {
            this.filterMap.put("LIST_BYCOLUMN", true);
            for (String column: columns) {
                this.filterMap.put(column + "_column", true);
            }
        }
        if (this.sortColumns.length() > 1) {
            this.filterMap.put("sortColumns", this.sortColumns);
        }
        if (this.appendCondition.length() > 1) {
            this.filterMap.put("appendCondition", this.appendCondition);
        }

        List<T> resultList = this.defaultDAO.getSqlSession().selectList(this.listStatement, this.filterMap, new RowBounds( (page - 1) * pageSize, pageSize));
        pageInfo.setData(resultList);
        pageInfo.setRecordsTotal(this.getCount(this.filterMap));

        if(columns != null) {
            this.filterMap.remove("LIST_BYCOLUMN");
            for (String column : columns) {
                this.filterMap.remove(column + "_column");
            }
        }
        return pageInfo;
    }

    @Override
    public Query<T> order(String column, Order order) {
        if (this.hasSortColumn) {
            this.sortColumns.append(" ,");
        }

        this.sortColumns.append(column).append("  " + order.toString());
        this.hasSortColumn = true;
        return this;
    }

    @Override
    public Query<T> condition(String condition) {
        this.appendCondition.append(condition);
        return this;
    }

    private Long getCount(Object parameter) throws Exception {
        MappedStatement ms = this.defaultDAO.getSqlSession().getConfiguration().getMappedStatement(this.listStatement);
        BoundSql boundSql = ms.getBoundSql(parameter);
        DefaultParameterHandler dp = new DefaultParameterHandler(ms, parameter, boundSql);
        Connection connection;
        PreparedStatement countStmt = null;
        SqlSessionTemplate temp = null;
        DefaultSqlSession defaultSession = null;

        try {
            temp = (SqlSessionTemplate)this.defaultDAO.getSqlSession();
            defaultSession = (DefaultSqlSession) SqlSessionUtils.getSqlSession(temp.getSqlSessionFactory(), temp.getExecutorType(), temp.getPersistenceExceptionTranslator());
            connection = defaultSession.getConnection();
//            DatabaseIdProvider provider = new DefaultDatabaseIdProvider();
//            String dataSourceId = provider.getDatabaseId(ms.getConfiguration().getEnvironment().getDataSource());
//            SQLPage sqlPage = new SQLPage(dataSourceId);
//            String countSql = sqlPage.getCountQuery(boundSql.getSql());
            String countSql = "SELECT COUNT(*)" + boundSql.getSql().substring(boundSql.getSql().indexOf(" FROM "));
            countStmt = connection.prepareStatement(countSql);
            dp.setParameters(countStmt);
            ResultSet rs = countStmt.executeQuery();
            Long result = 0L;
            if (rs.next()) {
                result = rs.getLong(1);
            }
            rs.close();

            if (!SqlSessionUtils.isSqlSessionTransactional(defaultSession, temp.getSqlSessionFactory())) {
                defaultSession.commit(true);
            }
            return result;
        } finally {
            try {
                if (defaultSession != null) {
                    SqlSessionUtils.closeSqlSession(defaultSession, temp.getSqlSessionFactory());
                }
                if (countStmt != null) {
                    countStmt.close();
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }
}
