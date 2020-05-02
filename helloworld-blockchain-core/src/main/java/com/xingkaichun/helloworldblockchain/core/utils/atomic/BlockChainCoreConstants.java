package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * 常量工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainCoreConstants {

    //字符集
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    //第一个区块的高度
    public final static BigInteger FIRST_BLOCK_HEIGHT = BigInteger.ONE;
    //第一个区块的PREVIOUS_HASH
    public final static String FIRST_BLOCK_PREVIOUS_HASH = "xingkaichun";
    //区块链的链ID
    public final static String BLOCK_CHAIN_ID = "0001";
    //区块链版本 约束最大区块  强制升级
    public final static int BLOCK_CHAIN_VERSION = 1;


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
    //最小交易手续费
    public final static BigDecimal MIN_TRANSACTION_FEE = new BigDecimal("1");
    //交易文本字符串最大长度值
    public final static long TRANSACTION_TEXT_MAX_SIZE = 1024;
    //区块最多含有的交易数量
    public final static long BLOCK_MAX_TRANSACTION_SIZE = 10000;
    //nonce最大值
    public final static BigInteger MAX_NONCE = new BigInteger(String.join("", Collections.nCopies(50, "9")));
    //nonce最小值
    public final static BigInteger MIN_NONCE = BigInteger.ZERO;


    //挖矿设置
    //每轮挖矿最大时长。挖矿时间太长，则新提交的交易就很延迟才能包含到区块里。
    public final static long MAX_MINE_TIMESTAMP = 1 * 60 * 60 * 1000;

    //交易时间设置
    //交易最大滞后区块时间
    public final static long TRANSACTION_TIMESTAMP_MAX_AFTER_CURRENT_TIMESTAMP = 24 * 60 * 60 * 1000;
    //交易最大超前区块时间
    public final static long TRANSACTION_TIMESTAMP_MAX_BEFORE_CURRENT_TIMESTAMP = 24 * 60 * 60 * 1000;

}
