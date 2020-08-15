package com.xingkaichun.helloworldblockchain.core.utils;

import java.math.BigDecimal;

/**
 * Number工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class NumberUtil {

    /**
     * 判断字符串是否是一个大于等于0的整数或小数
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        try {
            String reg = "^-?[0-9]+(.[0-9]+)?$";
            return str.matches(reg);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取小数位数
     * @return 如果是整数，返回0
     */
    public static long obtainDecimalPlaces(BigDecimal bigDecimal){
        String plainString = bigDecimal.toPlainString();
        int indexOf = plainString.indexOf(".");
        if(indexOf < 0){
            return 0;
        }
        return plainString.length() - 1 - indexOf;
    }
}
