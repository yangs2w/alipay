package com.yalogs.alipay.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ClassName JsonUtils
 * @Description Json工具类
 * @Author yangs
 * @Date 6/10/2019 9:19 PM
 * @Version 1.0
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     *
     * @Author yangs
     * @Description 将对象转为一个字符串
     * @Date 6/10/2019 9:21 PM
     * @Param [object: 要转换的对象]
     * @Return java.lang.String
     */  
    public static String objectToJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
