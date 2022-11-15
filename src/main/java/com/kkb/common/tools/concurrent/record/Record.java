package com.kkb.common.tools.concurrent.record;

import java.lang.annotation.*;

/**
 * 用于绑定当前 request中的 record 记录器
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Record {
}
