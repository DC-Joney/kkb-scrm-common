package com.kkb.common.tools.concurrent.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

import java.lang.reflect.Field;

/**
 * 创建错误的日志信息并且在控制台进行打印
 *
 * @author zhangyang
 */
@Slf4j
@UtilityClass
public class DataBinderUtils {


    public <T> FieldError createFieldError(T t, Field field, Object originValue, String errorMessage) {

        Assert.notNull(t, "Create error instance must not be null");
        Assert.notNull(field, "Create error field must not be null");

        return createFieldError(t.getClass().getSimpleName(), field.getName(), originValue, true, errorMessage);
    }


    /**
     * 绑定错误时，处理
     *
     * @param className    绑定错误的类
     * @param fieldName    绑定错误的字段名称
     * @param originValue  绑定前的错误数据
     * @param errorMessage 错误信息
     */
    public FieldError createFieldError(String className, String fieldName, Object originValue, boolean bindingFailure, String errorMessage) {

        if (log.isDebugEnabled()) {

            if (!StringUtils.hasText(className) || !StringUtils.hasText(fieldName))
                log.warn("ClassName or filedName is empty when creating FieldError instance");
        }


        return new DirectionFieldError(className, fieldName, originValue, bindingFailure, errorMessage);
    }


    private static class DirectionFieldError extends FieldError {


        /**
         * 并不需要依赖于配置文件中的 错误提示 所以将 {@param codes} 和 {@param arguments} 置为null即可
         *
         * @param className      错误的类名称
         * @param field          错误的字段名称
         * @param rejectedValue  绑定出错的原始值
         * @param bindingFailure 是否绑定失败
         * @param errorMessage   失败理由
         */
        private DirectionFieldError(String className, String field, Object rejectedValue, boolean bindingFailure, String errorMessage) {
            super(className, field, rejectedValue, bindingFailure, null, null, errorMessage);
        }


        @Override
        public String toString() {
            return "字典校验失败: Validate error in class '" + getObjectName() + "' on field '" + this.getField() +
                    "': rejected value [" + this.getRejectedValue() + "]; Error message [ " + getDefaultMessage() + "]";
        }
    }

}
