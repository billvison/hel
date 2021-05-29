package com.xingkaichun.helloworldblockchain.setting;

/**
 * 区块链配置
 * 这里的任何配置的修改都可能导致与区块链网络不兼容，请慎重修改。
 *
 * @author 邢开春 409060350@qq.com
 */
public class Setting {

    //区块链网络使用的端口
    public static final int PORT = 8888;
    //区块链网络中的种子节点
    public static final String[] SEED_NODES = new String[]{"139.9.125.122","119.3.57.171"};

    /**
     * 创世区块
     */
    public static class GenesisBlockSetting {
        //创世区块的高度
        public static final long HEIGHT = 0;
        //创世区块的哈希
        public static final String HASH = "0000000000000000000000000000000000000000000000000000000000000000";
        //创世区块的挖矿难度
        public static final String DIFFICULTY = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    }

    /**
     * 激励设置
     */
    public static class IncentiveSetting {
        //挖出一个区块的期望耗时时间(单位：毫秒)
        public static final long BLOCK_TIME = 1000 * 60;
        //一个挖矿难度周期内的区块数量
        public static final long INTERVAL_BLOCK_COUNT = 14;
        //一个挖矿周期内的期望周期耗时时间
        public static final long INTERVAL_TIME = BLOCK_TIME * INTERVAL_BLOCK_COUNT;
    }

    /**
     * 区块设置
     */
    public static class BlockSetting {
        //区块最多含有的交易数量(1秒1个)
        public static final long BLOCK_MAX_TRANSACTION_COUNT = IncentiveSetting.BLOCK_TIME / 1000;
        //区块的存储字符容量限制：限制区块的最大字符数量
        public static final long BLOCK_MAX_SIZE = 1024 * 1024;
        //随机数的存储字符容量限制：限制随机数的字符只能为64个
        public static final long NONCE_SIZE = 64;
    }

    /**
     * 交易设置
     */
    public static class TransactionSetting {
        //交易的存储字符容量限制：限制交易的最大字符数量
        public static final long TRANSACTION_MAX_SIZE = 8888;
    }

    /**
     * 脚本设置
     */
    public static class ScriptSetting {
        //脚本的存储字符容量限制：限制脚本的最大字符数量
        public static final long SCRIPT_MAX_SIZE = 1024;
    }



    /**
     * 系统版本，这里的版本是一个区块高度
     * 要求：[区块的高度]必须要小于[区块链版本]，系统才能正常运行。
     */
    public static class SystemVersionSetting {
        /**
         * 版本列表
         */
        public static final long[] BLOCK_CHAIN_VERSION_LIST = new long[]{10000L};

        /**
         * 检查系统版本是否支持。
         */
        public static boolean isVersionLegal(long blockHeight){
            return blockHeight <= BLOCK_CHAIN_VERSION_LIST[BLOCK_CHAIN_VERSION_LIST.length-1];
        }
    }
}
