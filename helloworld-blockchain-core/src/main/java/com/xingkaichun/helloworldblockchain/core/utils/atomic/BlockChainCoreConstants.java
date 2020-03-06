package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import java.math.BigDecimal;
import java.nio.charset.Charset;

/**
 * 常亮工具类
 */
public class BlockChainCoreConstants {

    //字符集
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    //第一个区块的高度
    public final static int FIRST_BLOCK_HEIGHT = 1;
    //第一个区块的PREVIOUS_HASH
    public final static String FIRST_BLOCK_PREVIOUS_HASH = "xingkaichun";

    //交易金额的小数点保留位数限制
    public final static long TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES = 8;
    //最大交易金额
    public final static BigDecimal TRANSACTION_MAX_AMOUNT = new BigDecimal("1000000000000000000000000");
    //最小交易金额
    public final static BigDecimal TRANSACTION_MIN_AMOUNT = new BigDecimal("0.00000001");
    //交易文本尺寸最大值
    public final static long TRANSACTION_TEXT_MAX_SIZE = 10240;
    //区块最大含有的交易数量
    public final static long BLOCK_MAX_TRANSACTION_SIZE = 10000;
}
