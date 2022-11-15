package com.kkb.common.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kkb.common.core.exception.KkbBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReflectUtils {

    private final static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    private final static String defaultFormat = "yyyy-MM-dd HH:mm:ss";

    private final static DateTimeFormatter df = DateTimeFormatter.ofPattern(defaultFormat);

    /**
     * @description 修改对LocalDateTime支持
     * @author zyz
     * @since 2020-09-21
     */
    public static <T> T getBean(Map<String, Object> map, Class clazz) throws Exception {
        Object obj = clazz.newInstance();
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String propertyName = entry.getKey();
                Object value = entry.getValue();
                if (value == null || value == "null" || "".equals(value)) {
                    continue;
                }
                String setMethodName = "set"
                        + propertyName.substring(0, 1).toUpperCase()
                        + propertyName.substring(1);
                Field field = getClassField(clazz, propertyName);
                if (field == null) {
                    continue;
                }
                Class<?> fieldTypeClass = field.getType();
                value = convertValType(value, fieldTypeClass);
                try {
                    if (fieldTypeClass == LocalDateTime.class) {
                        JsonFormat[] jfs = field.getAnnotationsByType(JsonFormat.class);
                        if (jfs != null && jfs.length > 0) {
                            JsonFormat jf = jfs[0];
                            String format = jf.pattern();
                            if (!defaultFormat.equals(format)) {
                                clazz.getMethod(setMethodName, field.getType()).invoke(obj,
                                        LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern(format)));
                                continue;
                            }
                        }
                        clazz.getMethod(setMethodName, field.getType()).invoke(obj,
                                LocalDateTime.parse(value.toString(), df));
                    } else {
                        clazz.getMethod(setMethodName, field.getType()).invoke(obj, value);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) obj;
    }


    public static Field getClassField(Class<?> clazz, String fieldName) {
        if (Object.class.getName().equals(clazz.getName())) {
            return null;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {// 简单的递归一下
            return getClassField(superClass, fieldName);
        }
        return null;
    }

    private static List<Field> getAllClassField(Class<?> clazz) {
        List<Field> fields = new LinkedList<>();
        if (Object.class.getName().equals(clazz.getName())) {
            return fields;
        }
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static Object convertValType(Object value, Class<?> fieldTypeClass) {
        Object retVal = null;
        try {
            if (Long.class.getName().equals(fieldTypeClass.getName())
                    || long.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Long.parseLong(value.toString());
            } else if (Integer.class.getName().equals(fieldTypeClass.getName())
                    || int.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Integer.parseInt(value.toString());
            } else if (Float.class.getName().equals(fieldTypeClass.getName())
                    || float.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Float.parseFloat(value.toString());
            } else if (Double.class.getName().equals(fieldTypeClass.getName())
                    || double.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Double.parseDouble(value.toString());
            } else {
                retVal = value;
            }
        } catch (Exception e) {
            throw KkbBusinessException.of(e.getStackTrace()[0].getClassName() + "参数:" + value + "转化成:" + fieldTypeClass.getName() + "错误");
        }
        return retVal;
    }

    public static Map<String, Object> getMap(Object object) {
        List<Field> allClassField = getAllClassField(object.getClass());
        Map<String, Object> map = new HashMap<>();
        allClassField.stream().forEach(v -> {
            try {
                map.put(v.getName(), v.get(object));
            } catch (IllegalAccessException e) {
            }
        });
        return map;
    }

    /**
     * @Description 判断修改的字段
     * @Author: tong
     * @Date: 2020/6/15 4:48 下午
     * @Param: [entity, bean]
     * @return: java.util.List<java.lang.String>
     **/
    public static boolean contrastObj(Object entity, Object bean) {
        try {
            // 通过反射获取类的类类型及字段属性
            Class clazz = entity.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 排除序列化属性
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
                // 获取对应属性值
                Method getMethod = descriptor.getReadMethod();
                Object entityValue = getMethod.invoke(entity);
                Object beanValue = getMethod.invoke(bean);
                if (entityValue == null || beanValue == null) {
                    continue;
                }
                if (!entityValue.equals(beanValue)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ReflectUtils contrastObj e: {}", e);
        }
        return false;
    }

}
