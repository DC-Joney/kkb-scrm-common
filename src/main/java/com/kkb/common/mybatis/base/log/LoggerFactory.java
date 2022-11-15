package com.kkb.common.mybatis.base.log;


import com.kkb.common.mybatis.base.log.self4j.Slf4jLogger;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：LoggerFactory
 * 类描述：日志工厂方法类
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:04:11
 * version 2.0
 */
public class LoggerFactory {
    /**
     * Return logger by name
     *
     * @param name
     * @return Logger
     */
    public static Logger getLogger(String name) {
        org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(name);
        return new Slf4jLogger(logger);
    }

    /**
     * Return logger by class
     *
     * @param clazz
     * @return Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(clazz);
        return new Slf4jLogger(logger);
    }
}
