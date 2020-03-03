package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.xingkaichun.helloworldblockchain.core.exception.BlockChainCoreException;

import java.math.BigDecimal;

public class NumberUtil {

    /**
     * 判断字符串是否是一个大于等于0的整数或小数
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        try {
            String reg = "^[0-9]+(.[0-9]+)?$";
            return str.matches(reg);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取小数位数
     * @return 如果是整数，返回0
     */
    public static long decimalPlaces(BigDecimal transactionAmount){
        if(transactionAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BlockChainCoreException("本方法不支持负数");
        }
        String stringTransactionAmount = transactionAmount.toString();
        int indexOf = stringTransactionAmount.indexOf(".");
        if(indexOf < 0){
            return 0;
        }
        return stringTransactionAmount.length() - 1 - indexOf;
    }
}
