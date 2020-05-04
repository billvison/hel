package com.xingkaichun.helloworldblockchain.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 常量工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainCoreConstant {

    //全局字符编码
    public static final Charset GLOBAL_CHARSET = Charset.forName("UTF-8");
    //区块链的链ID
    public final static String BLOCK_CHAIN_ID = "0001";


    /**
     * 系统版本
     *
     * @author 邢开春 xingkaichun@qq.com
     */
    public static class SystemVersionConstant{
        /**
         * 区块链版本设置
         * 这里的版本是一个时间戳数值
         * 部分配置需要根据版本时间戳去获取
         * 第一版本只支持运行至北京时间2020-06-01 00:00:00，到时间后必须升级系统
         */
        //第一版本
        public final static long BLOCK_CHAIN_VERSION_1 = 1590940800000L;
        //版本列表
        public final static List<Long> BLOCK_CHAIN_VERSION_LIST = new ArrayList<>();
        static {
            BLOCK_CHAIN_VERSION_LIST.add(BLOCK_CHAIN_VERSION_1);
        }

        /**
         * 检查系统版本是否支持。
         */
        public static boolean isVersionLegal(long timestamp){
            if(timestamp > BLOCK_CHAIN_VERSION_LIST.get(BLOCK_CHAIN_VERSION_LIST.size()-1)){
                return false;
            }
            return true;
        }
        /**
         * 获得系统版本。
         */
        public static long obtainVersion(){
            return BLOCK_CHAIN_VERSION_LIST.get(BLOCK_CHAIN_VERSION_LIST.size()-1);
        }
    }

    /**
     * 创始区块
     *
     * @author 邢开春 xingkaichun@qq.com
     */
    public static class GenesisBlockConstant{
        //第一个区块的高度
        public final static BigInteger FIRST_BLOCK_HEIGHT = BigInteger.ONE;
        //第一个区块的PREVIOUS_HASH
        public final static String FIRST_BLOCK_PREVIOUS_HASH = "xingkaichun";
    }

    /**
     * 挖矿设置
     *
     * @author 邢开春 xingkaichun@qq.com
     */
    public static class MinerConstant{
        //产生区块的平均时间
        public final static long GENERATE_BLOCK_AVERAGE_TIMESTAMP = 2 *  60 * 1000;
        //初始化产生区块的难度
        public final static String INIT_GENERATE_BLOCK_DIFFICULTY_STRING = "000000";
        //初始化挖矿激励金额
        public final static BigDecimal INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT = new BigDecimal("100");
        //挖矿激励减产周期
        public final static long MINE_BLOCK_INCENTIVE_REDUCE_BY_HALF_INTERVAL_TIMESTAMP = 1 * 24 * 60 * 60 * 1000;

        //每轮挖矿最大时长。挖矿时间太长，则新提交的交易就很延迟才能包含到区块里。
        public final static long MAX_MINE_TIMESTAMP = 1 * 60 * 60 * 1000;

        //交易最大滞后区块时间
        public final static long TRANSACTION_TIMESTAMP_MAX_AFTER_CURRENT_TIMESTAMP = 24 * 60 * 60 * 1000;
        //交易最大超前区块时间
        public final static long TRANSACTION_TIMESTAMP_MAX_BEFORE_CURRENT_TIMESTAMP = 24 * 60 * 60 * 1000;
    }

    /**
     * 交易设置
     *
     * @author 邢开春 xingkaichun@qq.com
     */
    public static class TransactionConstant{
        //交易金额的小数点保留位数限制
        public final static int TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES = 0;
        //最大交易金额
        public final static BigDecimal TRANSACTION_MAX_AMOUNT = new BigDecimal("1000000000000000000000000");
        //最小交易金额
        public final static BigDecimal TRANSACTION_MIN_AMOUNT = new BigDecimal("1");
        //最小交易手续费
        public final static BigDecimal MIN_TRANSACTION_FEE = new BigDecimal("1");
    }
}
