package com.xingkaichun.helloworldblockchain.util;

import com.google.common.base.Strings;

import java.nio.charset.StandardCharsets;

/**
 * String工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class StringUtil {

    public static boolean isEquals(String string,String anotherString){
        if(string == anotherString){
            return true;
        }
        if(string == null || anotherString == null){
            return false;
        }
        return string.equals(anotherString);
    }

    public static boolean isNullOrEmpty(String string) {
        return Strings.isNullOrEmpty(string);
    }

    public static String format(String format, Object... args) {
        return String.format(format,args);
    }

    public static byte[] stringToUtf8Bytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String utf8BytesToString(byte[] bytes) {
        return new String(bytes,StandardCharsets.UTF_8);
    }
}
