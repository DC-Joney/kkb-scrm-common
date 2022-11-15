package com.kkb.common.tools.concurrent.utils;

import cn.hutool.core.util.StrUtil;
import com.kkb.common.core.exception.KkbBusinessException;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 摘抄自 {@link org.springframework.util.Assert}
 *
 * @author zhangyang
 * @apiNote 简单断言业务信息类
 * @see org.springframework.util.Assert
 */
@UtilityClass
public class BusinessAssert {


    /**
     * 判断是否为True
     */
    public void isTrue(boolean expression, String message) {
        if (!expression) {
            throw KkbBusinessException.of(message);
        }
    }

    /**
     * 判断是否为True
     */
    public void isTrue(boolean expression, String message, Object... param) {
        if (!expression) {
            throw KkbBusinessException.of(StrUtil.format(message, param));
        }
    }


    /**
     * 判断是否为True
     */
    public void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw KkbBusinessException.of(Objects.requireNonNull(messageSupplier.get(), "The message must not be null"));
        }
    }


    /**
     * 判断是否为True
     */
    public <E, R> void equal(E left, R right, String message) {
        if (!left.equals(right)) {
            throw KkbBusinessException.of(message);
        }
    }


    /**
     * 判断是否为空
     */
    public void isNull(Object object, String message) {
        if (object != null) {
            throw KkbBusinessException.of(message);
        }
    }

    /**
     * 判断是否为空
     */
    public void isNull(Object object, String messageTemplate, Object... params) {
        if (object != null) {
            throw KkbBusinessException.of(StrUtil.format(messageTemplate, params));
        }
    }


    /**
     * 判断是否不为空
     */
    public void notNull(Object object, String message) {
        if (object == null) {
            throw KkbBusinessException.of(message);
        }
    }

    /**
     * 判断是否不为空
     */
    public void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw KkbBusinessException.of(String.format(message, args));
        }
    }


    /**
     * 判断字符串是否有值（不包含空字符串）
     */
    public void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw KkbBusinessException.of(message);
        }
    }


    /**
     * 判断是否包含某个字符串
     *
     * @param textToSearch 要被查找的字符串
     * @param substring    查找被包含的字符串
     */
    public static void doesNotContain(String textToSearch, String substring, String message) {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
            throw KkbBusinessException.of(message);
        }
    }


    /**
     * 判断数据是否为空
     */
    public static void notEmpty(Object[] array, String message) {
        if (ObjectUtils.isEmpty(array)) {
            throw KkbBusinessException.of(message);
        }
    }


    /**
     * 判断数据是否有为null的元素
     */
    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw KkbBusinessException.of(message);
                }
            }
        }
    }


    /**
     * 判断集合是否为空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw KkbBusinessException.of(message);
        }
    }

    /**
     * 判断map 是否为空
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw KkbBusinessException.of(message);
        }
    }

    /**
     * 判断该实例是否是某个类的实例
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            instanceCheckFailed(type, obj, message);
        }
    }


    /**
     * 判断该实例是否是某个类的实例
     */
    public static void isInstanceOf(Class<?> type, Object obj) {
        isInstanceOf(type, obj, "");
    }

    /**
     * 判断某个字符串是否有值（包含空白字符串）
     */
    public void hasLength(String text, String message) {
        if (!StringUtils.hasLength(text)) {
            throw KkbBusinessException.of(message);
        }
    }


    private static void instanceCheckFailed(Class<?> type, Object obj, String msg) {
        String className = obj != null ? obj.getClass().getName() : "null";
        String result = "";
        boolean defaultMessage = true;
        if (StringUtils.hasLength(msg)) {
            if (endsWithSeparator(msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName(msg, className);
                defaultMessage = false;
            }
        }

        if (defaultMessage) {
            result = result + "Object of class [" + className + "] must be an instance of " + type;
        }

        throw KkbBusinessException.of(result);
    }

    private static boolean endsWithSeparator(String msg) {
        return msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith(".");
    }

    private static String messageWithTypeName(String msg, Object typeName) {
        return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
    }

}
