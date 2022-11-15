package com.kkb.common.send.ding;

import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 配置默认的corgi 访问地址
 * @author zhangyang
 */
public class CorgiEnvironmentPostprocessor implements EnvironmentPostProcessor {

    private static final String CORGI_URL_PROPERTY = "kkb.mos.service.url.scrm-corgi";
    private static final String DEFAULT_CORGI_URL = "http://kkb-scrm-corgi:8080/kkb-scrm-corgi";
    public static final String EXTENSION_PROPERTY_NAME = "extensionProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        if (StringUtils.hasText(environment.getProperty(CORGI_URL_PROPERTY))) {
            Map<String, Object> propertyMap = Maps.newHashMapWithExpectedSize(2);
            propertyMap.put(CORGI_URL_PROPERTY, DEFAULT_CORGI_URL);
            PropertySource<Map<String, Object>> propertySource = new MapPropertySource(EXTENSION_PROPERTY_NAME, propertyMap);
            propertySources.addLast(propertySource);
        }
    }
}
