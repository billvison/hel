package com.xingkaichun.helloworldblockchain.core.utils;

/**
 * Long工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class LongUtil {
    public static final long ZERO = 0;
    public static final long ONE = 1;


    public static boolean isEquals(long long1,long long2){
        return long1 == long2;
    }

    public static boolean isLessThan(long long1,long long2){
        return long1 < long2;
    }

    public static boolean isLessEqualThan(long long1, long long2) {
        return long1 <= long2;
    }

    public static boolean isGreatThan(long long1, long long2) {
        return long1 > long2;
    }

    public static boolean isGreatEqualThan(long long1, long long2) {
        return long1 >= long2;
    }
}
