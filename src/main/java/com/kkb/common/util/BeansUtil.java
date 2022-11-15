package com.kkb.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * @Author WuSong
 * @Date 2019-02-25
 * @Time 14:30:37
 */
@Slf4j
public class BeansUtil {
    /**
     * @param source 要拷贝的对象
     * @return
     * @Description <p>获取到对象中属性为null的属性名  </P>
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * @param source 源对象
     * @param target 目标对象
     * @Description <p> 拷贝非空对象属性值 </P>
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }


    public static Map<String, Object> bean2map(Object bean) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //获取JavaBean的描述器
        BeanInfo b = Introspector.getBeanInfo(bean.getClass(), Object.class);
        //获取属性描述器
        PropertyDescriptor[] pds = b.getPropertyDescriptors();
        //对属性迭代
        for (PropertyDescriptor pd : pds) {
            //属性名称
            String propertyName = pd.getName();
            //属性值,用getter方法获取
            Method m = pd.getReadMethod();
            Object properValue = m.invoke(bean);//用对象执行getter方法获得属性值

            //把属性名-属性值 存到Map中
            if (properValue != null) {
                map.put(propertyName, properValue);
            }
        }
        return map;
    }


    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean == null) {
            return map;
        }
        if (bean instanceof Map) {
            return (Map<String, Object>) bean;
        }
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                declaredField.setAccessible(true);
                Object o = declaredField.get(bean);
                if (o != null) {
                    if (o instanceof String) {
                        if (StringUtils.isNotBlank(o.toString())) {
                            map.put(declaredField.getName(), o);
                        }
                    } else {
                        map.put(declaredField.getName(), o);
                    }
                }
            } catch (Exception e) {
            }
        }
        return map;
    }


    /**
     * 注意:自己去校验赋值是否成功
     *
     * @param source
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> T copyMapToBean(Map source, T bean) {
        Assert.notNull(source, "source must not be null");
        Assert.notNull(bean, "bean must not be null");
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(bean.getClass());
        Arrays.stream(targetPds).forEach(
                field -> {
                    try {
                        Object value = source.get(field.getName());
                        Method writeMethod = field.getWriteMethod();
                        if (Objects.nonNull(value) && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], value.getClass())) {
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }

                            writeMethod.invoke(bean, value);

                        }
                    } catch (Exception e) {
                        log.error("BeansUtil.copyMapToBean property:{} property type:{}  error->", field.getName(), field.getPropertyType(), e);
                    }

                }

        );

        return bean;
    }

}
