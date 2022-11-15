package com.kkb.common.pool.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

import static com.kkb.common.pool.annotation.ThreadQueryPool.ASYNC_THREAD_POOL_QUERY;

/**
 * 用于标识业务线程池
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier(ASYNC_THREAD_POOL_QUERY)
public @interface ThreadQueryPool {

    //查询线程池
    public static final String ASYNC_THREAD_POOL_QUERY="ASYNC_THREAD_POOL_QUERY";
}
