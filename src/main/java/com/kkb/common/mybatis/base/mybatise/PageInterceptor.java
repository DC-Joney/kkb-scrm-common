package com.kkb.common.mybatis.base.mybatise;

import com.kkb.common.mybatis.base.log.Logger;
import com.kkb.common.mybatis.base.log.LoggerFactory;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Properties;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：PageInterceptor
 * 类描述：分页插件
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:06:39
 * version 2.0
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class PageInterceptor implements Interceptor {
    public static final Logger log = LoggerFactory.getLogger(PageInterceptor.class);
    static int CONNECTION_INDEX = 0;

    public PageInterceptor() {
    }
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        statementHandler.getBoundSql();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        Connection connection = (Connection)invocation.getArgs()[CONNECTION_INDEX];
        String dataSourceProductName = connection.getMetaData().getDatabaseProductName();
        StatementHandler statementHandler_target;
        MetaObject metaStatementHandler_target;
        if (Proxy.isProxyClass(statementHandler.getClass())) {
            statementHandler_target = this.getTargetStatement(statementHandler);
            metaStatementHandler_target = MetaObject.forObject(statementHandler_target, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        } else {
            metaStatementHandler_target = metaStatementHandler;
        }

        RowBounds rowBounds = (RowBounds)metaStatementHandler_target.getValue("delegate.rowBounds");
        int offset = rowBounds.getOffset();
        int limit = rowBounds.getLimit();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql().trim();
        if (SQLPage.isSupport(dataSourceProductName) && (offset != 0 || limit != 2147483647)) {
            SQLPage sqlPagingSupport = new SQLPage(dataSourceProductName);
            sql = sqlPagingSupport.getPagedQuery(sql, offset, limit);
            metaStatementHandler_target.setValue("delegate.boundSql.sql", sql);
            metaStatementHandler_target.setValue("delegate.rowBounds.offset", 0);
            metaStatementHandler_target.setValue("delegate.rowBounds.limit", 2147483647);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private RoutingStatementHandler getTargetStatement(Object statementHandler) {
        Object statementHandler_target = null;
        if (Proxy.isProxyClass(statementHandler.getClass())) {
            Proxy var10000 = (Proxy)statementHandler;
            statementHandler_target = ReflectUtil.getFieldValue(Proxy.getInvocationHandler(statementHandler), "target");
            return this.getTargetStatement(statementHandler_target);
        } else {
            return (RoutingStatementHandler)statementHandler;
        }
    }

    private static class ReflectUtil {
        private ReflectUtil() {
        }
        static Object getFieldValue(Object obj, String fieldName) {
            Object result = null;
            Field field = getField(obj, fieldName);
            if (field != null) {
                field.setAccessible(true);
                try {
                    result = field.get(obj);
                } catch (IllegalArgumentException | IllegalAccessException var5) {
                    PageInterceptor.log.error("", var5);
                }
            }
            return result;
        }

        private static Field getField(Object obj, String fieldName) {
            Field field = null;
            Class clazz = obj.getClass();
            while(clazz != Object.class) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException var5) {
                    clazz = clazz.getSuperclass();
                }
            }
            return field;
        }
    }
}
