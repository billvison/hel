package com.xingkaichun.helloworldblockchain.core.utils;

import java.math.BigInteger;

/**
 * Equals工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class EqualsUtil {

    public static boolean isEquals(String obj1,String obj2){
        if(obj1 == obj2){
            return true;
        }
        if(obj1 == null || obj2 == null){
            return false;
        }
        return obj1.equals(obj2);
    }

    public static boolean isEquals(BigInteger obj1, BigInteger obj2){
        if(obj1 == obj2){
            return true;
        }
        if(obj1 == null || obj2 == null){
            return false;
        }
        return obj1.equals(obj2);
    }

    public static boolean isEquals(Integer obj1,Integer obj2){
        if(obj1 == obj2){
            return true;
        }
        if(obj1 == null || obj2 == null){
            return false;
        }
        return obj1.compareTo(obj2) == 0;
    }

    public static boolean isEquals(Long obj1,Long obj2){
        if(obj1 == obj2){
            return true;
        }
        if(obj1 == null || obj2 == null){
            return false;
        }
        return obj1.compareTo(obj2) == 0;
    }
}
