package com.kkb.common.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }

    public static byte[] toJsonBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }


    /**
     * 将字符串转Map对象
     *
     * @param jsonStr
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> json2Map(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> List<T> json2List(String jsonStr, Class<T> type) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(jsonStr, typeFactory.constructCollectionType(List.class, type));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * map 转 object
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T map2Obj(Map source, Class<T> targetClass) {
        if (null == source) {
            return null;
        }
        return objectMapper.convertValue(source, targetClass);
    }

    /**
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */

    public static <T> T parseJson(byte[] bytes, Class<T> type) throws IOException {
        return objectMapper.readValue(bytes, type);
    }

    public static <T> T parseJson(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    /**
     *
     *  使用的是jackson
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */

    public static <T> T parseJson2(String json, Class<T> clazz)throws IOException {

        return objectMapper.readValue(json, clazz);
    }

    /**
     * 使用的是fastjson
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseJson3(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }


  /*  public static <T> T parseByte(Byte[] bytes){
        objectMapper.
    }*/

    public static Object parseJson(String json, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }

    public static JsonNode parseJson(String jsonString) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        return jsonNode;

    }


    public static <T> T parseJsonNode(JsonNode jsonNode, String subKey, Class<T> tClass) throws IOException {
        JsonNode subJsonNode = jsonNode.get(subKey);
        if (Objects.isNull(subJsonNode)) {
            return null;
        }
        return objectMapper.readValue(subJsonNode.toString(), tClass);
    }

/*
    public static void main(String[] args) throws Exception {
        JsonNode jsonNode = JsonUtils.parseJson("{\"before\":null,\"after\":{\"ext\":null,\"note\":null,\"userInfo\":{\"nickName\":1,\"headUrl\":\"https://wework.qpic.cn/bizmail/Ojqzrf2DOePFcvjqjee9uxZoMlmOfngZsucZ7AAWLQpUfCSuhRcwWA/0\",\"imType\":\"enterpriseWeChat\",\"updateTime\":1635320350000,\"source\":\"kkb_hkz\",\"content\":\"{\\\"clientType\\\":\\\"mobile\\\"}\",\"thirdImId\":\"1688857976243057\",\"clientType\":\"mobile\",\"phone\":\"15910872394\",\"createTime\":1634797336000,\"appId\":\"45fdd039-b3e5-4ae6-9062-0efe8ff59c98\",\"id\":\"82498ad3-83f1-4b0c-8645-6e23e399a7af\",\"userType\":1,\"status\":\"ffd\"},\"clientType\":\"mobile\",\"createTime\":1635320351000,\"imUserId\":\"82498ad3-83f1-4b0c-8645-6e23e399a7af\",\"updateTime\":1635320351000,\"id\":\"4958490a-6e59-4672-9f3d-5fd77337ddd1\",\"friendUserInfo\":{\"unionid\":\"ocPQA1b-ymQyOeeW7SgNXsg2rBHo\",\"nickName\":\"Cooby\",\"headUrl\":\"http://wx.qlogo.cn/mmhead/8q8Immb88kKENzODLFELIJrtgIeDVmdHQOOxSw9mTZI/0\",\"imType\":\"enterpriseWeChat\",\"updateTime\":1635320351000,\"source\":\"kkb_hkz\",\"content\":\"{\\\"clientType\\\":\\\"mobile\\\"}\",\"thirdImId\":\"7881299503914699\",\"clientType\":\"mobile\",\"createTime\":1635320351000,\"appId\":\"45fdd039-b3e5-4ae6-9062-0efe8ff59c98\",\"id\":\"68b4e7fc-53f3-4d2e-a738-79832fefc0b0\",\"userType\":2,\"status\":1},\"imUserFriendId\":\"68b4e7fc-53f3-4d2e-a738-79832fefc0b0\",\"status\":\"sdfs\"}}");

        System.out.println(jsonNode.get("after"));
        JsonNode after = jsonNode.get("after");


        ImFriendDTO.UserInfo userInfo = JsonUtils.parseJsonNode(after, "userInfo", ImFriendDTO.UserInfo.class);

        System.out.println(userInfo);

        ImFriendDTO.FriendUserInfo friendUserInfo = JsonUtils.parseJsonNode(after, "friendUserInfo", ImFriendDTO.FriendUserInfo.class);

        System.out.println(friendUserInfo);


    }*/
}
