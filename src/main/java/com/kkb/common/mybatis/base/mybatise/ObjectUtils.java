package com.kkb.common.mybatis.base.mybatise;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *项目名称：kkb-srm-plugin-server
 * 类名称：ObjectUtils
 * 类描述：对象常用工具类方法
 * 创建人：YuanGL
 * 创建时间：2019年3月22日14:40:20
 * version 2.0
 */
public class ObjectUtils {

    public ObjectUtils() {
    }

    public static boolean isObjectEmpty(Object object)throws Exception {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return StringUtils.isEmpty(StringUtils.trimWhitespace((String)object));
        } else if (object instanceof Integer) {
            return object == null;
        } else if (object instanceof Long) {
            return object == null;
        } else {
            boolean allPropertiesStillEmpty;
            if (object instanceof List) {
                allPropertiesStillEmpty = true;

                Object listEntry;
                for(Iterator iter = ((List)object).iterator(); allPropertiesStillEmpty && iter.hasNext(); allPropertiesStillEmpty = isObjectEmpty(listEntry)) {
                    listEntry = iter.next();
                }
                return allPropertiesStillEmpty;
            } else {
                allPropertiesStillEmpty = true;
                Map<String, Object> properties = PropertyUtils.describe(object);
                Iterator iter = properties.entrySet().iterator();

                while(allPropertiesStillEmpty && iter.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)iter.next();
                    String key = (String)entry.getKey();
                    Object value = entry.getValue();
                    if (!"class".equals(key)) {
                        allPropertiesStillEmpty = isObjectEmpty(value);
                    }
                }
                return allPropertiesStillEmpty;
            }
        }
    }
}
