package com.kkb.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

public class JsonMapper {
    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    public static final JsonMapper INSTANCE = new JsonMapper();
    private ObjectMapper mapper;

    public JsonMapper() {
        this((JsonInclude.Include) null);
    }

    public JsonMapper(JsonInclude.Include include) {
        this.mapper = new ObjectMapper();
        if (include != null) {
            this.mapper.setSerializationInclusion(include);
        }

        this.mapper.setSerializationInclusion(include).setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        this.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public static JsonMapper nonNullMapper() {
        return new JsonMapper(JsonInclude.Include.NON_NULL);
    }

    public static JsonMapper nonEmptyMapper() {
        return new JsonMapper(JsonInclude.Include.NON_EMPTY);
    }

    public static JsonMapper defaultMapper() {
        return new JsonMapper();
    }

    public String toJson(Object object) throws JsonProcessingException {
            return this.mapper.writeValueAsString(object);
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) throws IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else {
                return this.mapper.readValue(jsonString, clazz);
        }
    }


    public <T> T fromJson(String jsonString, TypeReference typeReference) throws IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else {
                return this.mapper.readValue(jsonString, typeReference);
        }
    }

    public <T> T fromJson(String jsonString, JavaType javaType) throws IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else {
                return this.mapper.readValue(jsonString, javaType);
        }
    }

    public JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return this.mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return this.mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public void update(String jsonString, Object object) {
        try {
            this.mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException var4) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", var4);
        } catch (IOException var5) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", var5);
        }

    }

    public String toJsonP(String functionName, Object object) throws JsonProcessingException {
        return this.toJson(new JSONPObject(functionName, object));
    }

    public void enableEnumUseToString() {
        this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }
}
