package com.xingkaichun.helloworldblockchain.util;

/**
 * sqlite工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SqliteUtil {

    /**
     * sqlite没有boolean类型，用long类型代替，long转boolean
     */
    public static boolean longToBoolean(Long intValue){
        return Long.valueOf(0).compareTo(intValue) != 0;
    }
    /**
     * sqlite没有boolean类型，用long类型代替，boolean转long
     */
    public static long booleanToLong(Boolean booleanValue){
        if(booleanValue == null || !booleanValue){
            return 0;
        }
        return 1;
    }
}
