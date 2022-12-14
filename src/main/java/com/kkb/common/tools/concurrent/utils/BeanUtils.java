package com.kkb.common.tools.concurrent.utils;

import com.google.common.collect.Lists;
import com.kkb.common.tools.concurrent.utils.bean.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * {@link org.springframework.beans.annotation.AnnotationBeanUtils}
 * {@link org.springframework.beans.PropertyAccessorUtils}
 *
 * @author zhangyang
 */
@Slf4j
@UtilityClass
@SuppressWarnings("Duplicates")
public class BeanUtils extends org.springframework.beans.BeanUtils {

    @SuppressWarnings("rawtypes")
    private static <T> Constructor<T> defaultConstructor(Class<T> beanClass) {
        try {
            return beanClass.getDeclaredConstructor((Class[]) null);
        } catch (NoSuchMethodException e) {
            if (log.isDebugEnabled())
                log.debug("The beanClass dose not have default Constructor");
        }
        return null;
    }


    /**
     * org.springframework.beans.strategy.support.ConstructorResolver
     * <p>
     * {@link ConstructorArgumentValues}
     */
    public static <T, R> R copyProperties(T source, @NonNull Supplier<R> instanceSupplier, String... ignoreProperties) {
        R instance = instanceSupplier.get();
        Assert.notNull(instance, "The supplier produce instance must not be null");
        copyNotNullProperties(source, instance,false, ignoreProperties);
        return instance;
    }

    /**
     * ????????????Class??????????????????
     * @param source source instance
     * @param targetClass target class
     * @param ignoreProperties ??????????????????
     */
    public static <T, R> R copyPropertiesWithClass(T source, Class<R> targetClass, String... ignoreProperties) {
        Constructor<R> constructor = defaultConstructor(targetClass);
        Assert.notNull(constructor, "The targetClass" + targetClass + "have not default Constructor");
        R instance = org.springframework.beans.BeanUtils.instantiateClass(constructor);
        copyNotNullProperties(source, instance, false, ignoreProperties);
        return instance;
    }


    /**
     * ????????????Class??????????????????
     * @param source source instance
     * @param targetClass target class
     * @param ignoreProperties ??????????????????
     * @param callback ???????????????????????????????????????
     */
    public static <T, R> R copyPropertiesWithClass(T source, Class<R> targetClass, CopyPropertyCallBack<T, R> callback, String... ignoreProperties) {
        Constructor<R> constructor = defaultConstructor(targetClass);
        Assert.notNull(constructor, "The targetClass" + targetClass + "have not default Constructor");
        R instance = instantiateClass(constructor);
        copyProperties(source, instance, callback, ignoreProperties);
        return instance;
    }


    /**
     * ????????????Class??????????????????
     * @param sources source instances
     * @param targetClass target class
     * @param ignoreProperties ??????????????????
     */
    public static <T, R> List<R> copyPropertiesWithClass(List<T> sources, Class<R> targetClass, String... ignoreProperties) {
        List<R> ret = Lists.newArrayListWithCapacity(sources.size());
        sources.forEach((entity) -> ret.add(copyPropertiesWithClass(entity, targetClass, ignoreProperties)));
        return ret;
    }

    public static <T, R> List<R> copyPropertiesWithClass(List<T> sources, Class<R> targetClass, CopyPropertyCallBack<T, R> callback, String... ignoreProperties) {
        List<R> ret = Lists.newArrayListWithCapacity(sources.size());
        sources.forEach((entity) -> ret.add(copyPropertiesWithClass(entity, targetClass, callback, ignoreProperties)));
        return ret;
    }

    /**
     * ??????source????????????????????? ???target???
     * @param source source instance
     * @param target target instance
     * @param ignoreProperties ??????????????????
     * @param callback ???????????????????????????????????????
     */
    public static <T, R> void copyProperties(T source, R target, CopyPropertyCallBack<T, R> callback, String... ignoreProperties) throws BeansException {
        copyNotNullProperties(source, target, false, ignoreProperties);
        callback.copy(source, target);
    }

    /**
     * ??????source????????????????????????target???
     * @param source source instance
     * @param target target instance
     * @param ignoreProperties ??????????????????
     * @param callback ???????????????????????????????????????
     */
    public static <T, R> void copyNotNullProperties(T source, R target, CopyPropertyCallBack<T, R> callback, String... ignoreProperties) {
        copyNotNullProperties(source, target, true, ignoreProperties);
        callback.copy(source, target);
    }


    /**
     * ??????source????????????????????? ???target???
     * @param source source instance
     * @param target target instance
     * @param ignoreProperties ??????????????????
     * @param ignoreNull ????????????????????????
     */
    public static void copyNotNullProperties(Object source, Object target, boolean ignoreNull, String... ignoreProperties) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);

        List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();

            //?????????????????????????????????sourceClass????????????????????????propertyDescriptor???????????????alias?????????readDescriptor
            String aliasName = null;
            Field writeField = ReflectionUtils.findField(actualEditable, targetPd.getName());

            //TODO ???????????????????????????
            if (writeField != null) {
                Set<CopyAlias> copyAliases = AnnotationUtils.getRepeatableAnnotations(writeField, CopyAlias.class, CopyAliases.class);
                Optional<AnnotationAttributes> optionalCopyAlias = copyAliases.stream()
                        .map(copyAlias -> AnnotationUtils.getAnnotationAttributes(copyAlias,false,true))
                        .filter(copyAlias -> StringUtils.hasText(copyAlias.getString("name")))
                        .filter(copyAlias -> copyAlias.getClass("source").equals(source.getClass()))
                        .findFirst();

                if (optionalCopyAlias.isPresent())
                    aliasName = optionalCopyAlias.get().getString("name");

                Set<CopyIgnore> copyIgnores = AnnotationUtils.getRepeatableAnnotations(writeField, CopyIgnore.class, CopyIgnores.class);
                Optional<CopyIgnore> optionalCopyIgnore = copyIgnores.stream()
                        .filter(copyIgnore -> copyIgnore.source().equals(source.getClass()))
                        .findFirst();

                if (optionalCopyIgnore.isPresent() && optionalCopyIgnore.get().source().equals(source.getClass()))
                    continue;

                if (AnnotationUtils.getAnnotation(writeField, CopyIgnoreAll.class) != null)
                    continue;

            }

            if (writeMethod != null &&
                    (ignoreList == null || !ignoreList.contains(targetPd.getName()) || !ignoreList.contains(aliasName))) {

                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());

                //??????????????????sourceClass?????????????????????propertyDescriptor????????????alias?????????propertyDescriptor
                if (sourcePd == null && StringUtils.hasText(aliasName))
                    sourcePd = getPropertyDescriptor(source.getClass(), aliasName);

                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null) {
                        Class<?> writeType = writeMethod.getParameterTypes()[0];
                        Class<?> readType = readMethod.getReturnType();
                        //??????????????????????????????
                        boolean assignable = ClassUtils.isAssignable(writeType, readType);
                        //???????????????
                        ConversionService conversionService = ApplicationConversionService.getSharedInstance();
                        if (assignable || conversionService.canConvert(readType,writeType)) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }

                                Object value = readMethod.invoke(source);
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }

                                //?????? writeType???readType?????????
                                if (!assignable) {
                                    value = conversionService.convert(value, writeType);
                                }

                                if (ignoreNull) {
                                    if (!Objects.isNull(value)) {
                                        writeMethod.invoke(target, value);
                                    }
                                    continue;
                                }

                                writeMethod.invoke(target, value);
                            } catch (Throwable ex) {
                                throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * ???????????????????????????null????????????
     *
     * @param source ??????????????????
     */
    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper wrapper = new BeanWrapperImpl(source);
        //???Spring????????????????????????????????????????????????????????????,???beanWrpper?????????????????????
        PropertyDescriptor[] propertyDescriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(source.getClass());
        final Stream.Builder<String> builder = Stream.builder();
        for (PropertyDescriptor pd : propertyDescriptors) {
            Object srcValue = wrapper.getPropertyValue(pd.getName());
            if (srcValue == null) {
                builder.add(pd.getName());
            }
        }
        return builder.build().toArray(String[]::new);
    }


    /**
     * ????????? bean ??????????????????map??????
     *
     * @param bean ????????????map???bean ??????
     * @throws Exception
     */
    public static Map<String, Object> beanToMap(Object bean) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //??????JavaBean????????????
//        BeanInfo b = Introspector.getBeanInfo(bean.getClass(), Object.class);
        //?????????????????????
//        PropertyDescriptor[] pds = b.getPropertyDescriptors();
        PropertyDescriptor[] pds = getPropertyDescriptors(bean.getClass());
        //???????????????
        for (PropertyDescriptor pd : pds) {
            //????????????
            String propertyName = pd.getName();
            //?????????,???getter????????????
            Method m = pd.getReadMethod();
            Object properValue = m.invoke(bean);//???????????????getter?????????????????????

            //????????????-????????? ??????Map???
            if (properValue != null) {
                map.put(propertyName, properValue);
            }
        }
        return map;
    }


    /**
     * ??????:?????????????????????????????????
     *
     * @param source map?????????
     * @param bean   ????????????????????????bean??????
     */
    public static <T> T copyMapToBean(Map<?, ?> source, T bean) {
        Assert.notNull(source, "source must not be null");
        Assert.notNull(bean, "bean must not be null");
        PropertyDescriptor[] targetPds = getPropertyDescriptors(bean.getClass());
        PropertyDescriptor errorPd = null;
        try {
            for (PropertyDescriptor pd : targetPds) {
                errorPd = pd;
                Object value = source.get(pd.getName());
                Method writeMethod = pd.getWriteMethod();
                if (Objects.nonNull(value) && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], value.getClass())) {
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(bean, value);
                }
            }
        } catch (Exception e) {
            if (errorPd != null) {
                log.error("BeansUtil.copyMapToBean property:{} property type:{}  error->", errorPd.getName(), errorPd.getPropertyType(), e);
            }
        }
        return bean;
    }


    public interface CopyPropertyCallBack<T, R> {

        void copy(T origin, R result);

    }

}
