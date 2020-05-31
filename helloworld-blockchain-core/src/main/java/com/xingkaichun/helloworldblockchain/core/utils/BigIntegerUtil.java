package com.xingkaichun.helloworldblockchain.core.utils;

import java.math.BigInteger;

/**
 * BigInteger工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BigIntegerUtil {


    public static boolean isEquals(BigInteger bigInteger1,BigInteger bigInteger2){
        if(bigInteger1 == bigInteger2){
            return true;
        }
        if(bigInteger1 == null || bigInteger2 == null){
            return false;
        }
        return bigInteger1.compareTo(bigInteger2) == 0;
    }

    public static boolean isLessThan(BigInteger bigInteger1,BigInteger bigInteger2){
        return bigInteger1.compareTo(bigInteger2) < 0;
    }

    public static boolean isLessEqualThan(BigInteger bigInteger1, BigInteger bigInteger2) {
        return bigInteger1.compareTo(bigInteger2) <= 0;
    }

    public static boolean isGreatThan(BigInteger bigInteger1, BigInteger bigInteger2) {
        return bigInteger1.compareTo(bigInteger2) > 0;
    }

    public static boolean isGreatEqualThan(BigInteger bigInteger1, BigInteger bigInteger2) {
        return bigInteger1.compareTo(bigInteger2) >= 0;
    }
}
