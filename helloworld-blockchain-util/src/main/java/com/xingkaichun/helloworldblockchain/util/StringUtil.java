package com.xingkaichun.helloworldblockchain.util;

import com.google.common.base.Strings;

import java.util.Collections;

/**
 * String工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class StringUtil {

    public static final String BLANKSPACE = " ";


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

    public static String prefixPadding(String rawValue,int targetLength,String paddingValue) {
        if(rawValue.length() >= targetLength){
            return rawValue;
        }
        return String.join("", Collections.nCopies(targetLength-rawValue.length(), paddingValue)) + rawValue;
    }

    public static String concat(String value1,String value2) {
        return value1 + value2;
    }
    public static String concat3(String value1,String value2,String value3) {
        return value1 + value2 + value3;
    }
}
