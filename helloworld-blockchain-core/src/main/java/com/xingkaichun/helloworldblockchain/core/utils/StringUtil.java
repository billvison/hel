package com.xingkaichun.helloworldblockchain.core.utils;

/**
 * String工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class StringUtil {

    public static boolean isEquals(String obj1,String obj2){
        if(obj1 == obj2){
            return true;
        }
        if(obj1 == null || obj2 == null){
            return false;
        }
        return obj1.equals(obj2);
    }
}
