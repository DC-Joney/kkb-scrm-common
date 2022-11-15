package com.kkb.common.tools.concurrent.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link Function} 函数接口扩展
 *
 * @author zhangyang
 * @date 2020-10-19
 */
@UtilityClass
public class FunctionUtils {


    /**
     * 包装常量Function
     *
     * @param value 常量值
     */
    public <E, R> Function<E, R> constant(R value) {
        return s -> value;
    }


    /**
     * 空的Consumer 消费
     *
     */
    public <T> Consumer<T> consumer() {
        return s -> { };
    }


}
