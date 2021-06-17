package com.xingkaichun.helloworldblockchain.util;

/**
 * Long工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class NumberUtil {

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
