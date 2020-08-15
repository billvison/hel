package com.xingkaichun.helloworldblockchain.core.utils;

/**
 * sqlite工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SqliteUtil {

    /**
     * sqlite没有boolean类型，用int类型代替，int转boolean
     */
    public static boolean intToBoolean(Integer intValue){
        return Integer.valueOf(0).compareTo(intValue)==0?false:true;
    }
    /**
     * sqlite没有boolean类型，用int类型代替，boolean转int
     */
    public static int booleanToInt(Boolean booleanValue){
        if(booleanValue == null || !booleanValue){
            return 0;
        }
        return 1;
    }
}
