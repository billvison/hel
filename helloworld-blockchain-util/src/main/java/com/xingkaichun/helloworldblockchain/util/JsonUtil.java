package com.xingkaichun.helloworldblockchain.util;

import com.google.gson.Gson;

/**
 * JSON工具
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class JsonUtil {

    private static Gson GSON = new Gson();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json,classOfT);
    }
}
