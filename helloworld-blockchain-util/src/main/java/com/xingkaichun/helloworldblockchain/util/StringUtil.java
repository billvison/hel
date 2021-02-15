package com.xingkaichun.helloworldblockchain.util;

/**
 * String工具类
 *
 * @author 邢开春
 */
public class StringUtil {

    public static boolean isEquals(String str1,String str2){
        if(str1 == str2){
            return true;
        }
        if(str1 == null || str2 == null){
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }
}
