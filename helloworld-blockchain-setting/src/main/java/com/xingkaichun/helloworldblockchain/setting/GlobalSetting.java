package com.xingkaichun.helloworldblockchain.setting;

/**
 * 全局设置
 *
 * @author 邢开春 409060350@qq.com
 */
public class GlobalSetting {

    //区块链网络默认的节点端口
    public static final int DEFAULT_PORT = 8888;
    //区块链网络中的种子节点
    public static final String[] SEED_NODE_LIST = new String[]{"139.9.125.122","119.3.57.171"};

    /**
     * 创世区块
     */
    public static class GenesisBlock{
        //创世区块的高度
        public static final long HEIGHT = 0;
        //创世区块的哈希
        public static final String HASH = "0000000000000000000000000000000000000000000000000000000000000000";
        //创世区块的挖矿难度
        public static final String DIFFICULTY = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    }

    /**
     * 挖矿设置
     */
    public static class IncentiveConstant{
        //挖出一个区块的期望耗时时间
        public static final long BLOCK_TIME = 1000 * 60;
        //一个挖矿难度周期内的区块数量
        public static final long INTERVAL_BLOCK_COUNT = 14;
        //一个挖矿难度周期内的期望周期耗时时间
        public static final long INTERVAL_TIME = BLOCK_TIME * INTERVAL_BLOCK_COUNT;
    }

    /**
     * 区块设置
     */
    public static class BlockConstant {
        //区块最多含有的交易数量(1秒1个)
        public static final long BLOCK_MAX_TRANSACTION_COUNT = IncentiveConstant.BLOCK_TIME / 1000;
        //区块存储容量限制
        public static final long BLOCK_MAX_SIZE = 1024*1024;
        //nonce字符串的长度是64 64位十六进制数
        public static final long NONCE_SIZE = 64;
    }

    /**
     * 交易设置
     */
    public static class TransactionConstant {
        //交易文本字符串最大长度值
        public static final long TRANSACTION_MAX_SIZE = 8888;
    }

    /**
     * 脚本设置
     */
    public static class ScriptConstant{
        //脚本最大存储容量
        public static final long SCRIPT_MAX_SIZE = 1024;
    }



    /**
     * 系统版本，这里的版本是一个区块高度
     * 要求：[区块的高度]必须要小于[区块链版本]，系统才能正常运行。
     */
    public static class SystemVersionConstant{
        /**
         * 版本列表
         */
        public static final Long[] BLOCK_CHAIN_VERSION_LIST = new Long[]{10000L};

        /**
         * 检查系统版本是否支持。
         */
        public static boolean isVersionLegal(long blockHeight){
            return blockHeight <= BLOCK_CHAIN_VERSION_LIST[BLOCK_CHAIN_VERSION_LIST.length-1];
        }
    }



    /**
     * 分叉设置
     */
    public static class ForkConstant{
        //两个区块链有分叉时，区块差异数量大于这个值，则真的分叉了。
        public static final long FORK_BLOCK_COUNT = 100;
    }
}
