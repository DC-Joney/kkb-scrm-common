package com.kkb.common.pool.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

import static com.kkb.common.pool.annotation.ThreadBusinessPool.ASYNC_THREAD_POOL_BUSi_DEAL;

/**
 * 用于标识业务线程池
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
@Qualifier(ASYNC_THREAD_POOL_BUSi_DEAL)
public @interface ThreadBusinessPool {

    //事务线程池
    public static final String ASYNC_THREAD_POOL_BUSi_DEAL="ASYNC_THREAD_POOL_BUSS_DEAL";
}
