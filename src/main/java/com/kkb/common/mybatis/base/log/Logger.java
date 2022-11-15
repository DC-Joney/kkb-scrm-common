package com.kkb.common.mybatis.base.log;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：Logger
 * 类描述：日志接口类
 * 创建人：YuanGL
 * 创建时间：2019-3-25 11:03:58
 * version 2.0
 */
public interface Logger {

    String getName();

    void trace(String message);

    void trace(String format, Object... args);

    boolean isTraceEnabled();

    void debug(String message);

    void debug(String format, Object... args);
    
    boolean isDebugEnabled();

    void info(String message);

    void info(String format, Object... args);

    boolean isInfoEnabled();

    void warn(String message);

    void warn(String format, Object... args);

    boolean isWarnEnabled();

    void error(String message);

    void error(String format, Object... args);
    
    void error(Exception ex);
    
    void error(String message, Exception ex);

    boolean isErrorEnabled();
}
