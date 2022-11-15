package com.kkb.common.tools.concurrent.utils.bean;

import java.lang.annotation.*;

/**
 * 字段别名，用于 {@code copyProperty} 时，注入不同来源的属性值
 * @author zhangyang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CopyIgnores {

    /**
     * 源目标Class
     */
    CopyIgnore[] value() default {};
}
