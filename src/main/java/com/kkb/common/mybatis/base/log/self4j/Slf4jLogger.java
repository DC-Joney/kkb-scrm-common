package com.kkb.common.mybatis.base.log.self4j;



import com.kkb.common.mybatis.base.log.Logger;

import java.io.Serializable;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：Slf4jLogger
 * 类描述：日志通过self4j的实现类
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:03:42
 * version 2.0
 */
public class Slf4jLogger implements Logger, Serializable {

    private static final long serialVersionUID = 6752116279786475903L;

    public Slf4jLogger(org.apache.logging.log4j.Logger impl) {
        _impl = impl;
    }

    @Override
    public String getName() {
        return _impl.getName();
    }

    @Override
    public void trace(String message) {
        _impl.trace( message);
    }

    @Override
    public void trace(String format, Object... args) {
        _impl.trace(format, args);
    }

    @Override
    public boolean isTraceEnabled() {
        return _impl.isTraceEnabled();
    }

    @Override
    public void debug(String message) {
        _impl.debug( message);
    }

    @Override
    public void debug(String format, Object... args) {
        _impl.debug(format, args);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return _impl.isDebugEnabled();
    }

    @Override
    public void info(String message) {
        _impl.info( message);
    }

    @Override
    public void info(String format, Object... args) {
        _impl.info(format, args);
    }

    @Override
    public boolean isInfoEnabled() {
        return _impl.isInfoEnabled();
    }

    @Override
    public void warn(String message) {
        _impl.warn( message);
    }

    @Override
    public void warn(String format, Object... args) {
        _impl.warn(format, args);
    }

    @Override
    public boolean isWarnEnabled() {
        return _impl.isWarnEnabled();
    }

    @Override
    public void error(String message) {
        _impl.error( message);
    }

    @Override
    public void error(String format, Object... args) {
        _impl.error(format, args);
    }
    
    @Override
    public void error(Exception ex) {
        _impl.error(ex);
    }
    @Override
    public void error(String message,Exception ex) {
        _impl.error( message, ex);
    }
    
    @Override
    public boolean isErrorEnabled() {
        return _impl.isErrorEnabled();
    }

    private org.apache.logging.log4j.Logger _impl;
}
