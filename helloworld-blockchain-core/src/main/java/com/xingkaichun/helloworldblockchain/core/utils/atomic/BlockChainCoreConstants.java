package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * 常亮工具类
 */
public class BlockChainCoreConstants {

    //字符集
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    //第一个区块的高度
    public final static BigInteger FIRST_BLOCK_HEIGHT = BigInteger.ONE;
    //第一个区块的PREVIOUS_HASH
    public final static String FIRST_BLOCK_PREVIOUS_HASH = "xingkaichun";

    //产生区块的平均时间
    public final static long GENERATE_BLOCK_AVERAGE_TIMESTAMP = 2 *  60 * 1000;
    //初始化产生区块的难度
    public final static String INIT_GENERATE_BLOCK_DIFFICULTY_STRING = "000000";
    //初始化挖矿激励金额
    public final static BigDecimal INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT = new BigDecimal("100");
    //挖矿激励减产周期
    public final static long MINE_BLOCK_INCENTIVE_REDUCE_BY_HALF_INTERVAL_TIMESTAMP = 1 * 24 * 60 * 60 * 1000;

    //交易金额的小数点保留位数限制
    public final static int TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES = 8;
    //最大交易金额
    public final static BigDecimal TRANSACTION_MAX_AMOUNT = new BigDecimal("1000000000000000000000000");
    //最小交易金额
    public final static BigDecimal TRANSACTION_MIN_AMOUNT = new BigDecimal("0.00000001");
    //交易文本字符串最大长度值
    public final static long TRANSACTION_TEXT_MAX_SIZE = 10240;
    //区块最多含有的交易数量
    public final static long BLOCK_MAX_TRANSACTION_SIZE = 10000;
    //nonce最大值
    public final static BigInteger MAX_NONCE = new BigInteger(String.join("", Collections.nCopies(50, "9")));
    //nonce最小值
    public final static BigInteger MIN_NONCE = BigInteger.ZERO;
}
