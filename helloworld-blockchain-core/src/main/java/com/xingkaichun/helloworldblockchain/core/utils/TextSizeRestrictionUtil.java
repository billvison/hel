package com.xingkaichun.helloworldblockchain.core.utils;

import java.math.BigInteger;
import java.util.Collections;

/**
 * 存放有关存储容量有关的常量，例如区块最大的存储容量，交易最大的存储容量
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TextSizeRestrictionUtil {

    //交易文本字符串最大长度值
    public final static long TRANSACTION_TEXT_MAX_SIZE = 1024;
    //区块存储容量限制
    public final static long BLOCK_TEXT_MAX_SIZE = 1000 * 1024;
    //区块最多含有的交易数量
    public final static long BLOCK_MAX_TRANSACTION_SIZE = BLOCK_TEXT_MAX_SIZE/TRANSACTION_TEXT_MAX_SIZE;
    //nonce最大值
    public final static BigInteger MAX_NONCE = new BigInteger(String.join("", Collections.nCopies(50, "9")));
    //nonce最小值
    public final static BigInteger MIN_NONCE = BigInteger.ZERO;
}
