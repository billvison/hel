package com.xingkaichun.helloworldblockchain.util;

import com.google.common.base.Strings;

/**
 * String工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class StringUtil {

    public static boolean isEquals(String string,String anotherString){
        if(string == null){
            throw new RuntimeException("parameter not support null value.");
        }
        return string.equals(anotherString);
    }

    public static boolean isNullOrEmpty(String string) {
        return Strings.isNullOrEmpty(string);
    }

    public static String format(String format, Object... args) {
        return String.format(format,args);
    }
}
