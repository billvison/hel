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
        //期望的每个区块挖矿耗时时间
        public static final long BLOCK_TIME = 1000 * 60;
        //在一个挖矿难度周期内的区块数量
        public static final long INTERVAL_BLOCK = 14;
        //一个挖矿难度周期内的周期耗时时间
        public static final long INTERVAL_TIME = BLOCK_TIME * INTERVAL_BLOCK;
    }

    /**
     * 区块设置
     */
    public static class BlockConstant {
        //区块最多含有的交易数量(1秒1个)
        public static final long BLOCK_MAX_TRANSACTION_COUNT = IncentiveConstant.BLOCK_TIME / 1000;
        //区块存储容量限制
        public static final long BLOCK_TEXT_MAX_SIZE = TransactionConstant.TRANSACTION_TEXT_MAX_SIZE * BLOCK_MAX_TRANSACTION_COUNT;
        //nonce字符串的长度是64 64位十六进制数
        public static final long NONCE_TEXT_SIZE = 64;
    }

    /**
     * 交易设置
     */
    public static class TransactionConstant {
        //交易文本字符串最大长度值
        public static final long TRANSACTION_TEXT_MAX_SIZE = 1024;
    }

    /**
     * 脚本设置
     */
    public static class ScriptConstant{
        //脚本最大存储容量
        public static final long SCRIPT_TEXT_MAX_SIZE = 1024;
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
     * 挖矿设置
     */
    public static class MinerConstant{
        //这个时间间隔更新一次正在被挖矿的区块的交易。如果时间太长，可能导致新提交的交易延迟被确认。
        public static final long MINE_TIMESTAMP_PER_ROUND = 1000 * 10;
    }

    /**
     * 节点设置
     */
    public static class NodeConstant{
        //两个区块链有分叉时，区块差异数量大于这个值，则真的分叉了。
        public static final long FORK_BLOCK_SIZE = 100;
        //在区块链网络中自动搜寻新的节点的间隔时间
        public static final long SEARCH_NODE_TIME_INTERVAL = 1000 * 60 * 2;
        //在区块链网络中自动搜索节点的区块链高度
        public static final long SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL = 1000 * 60 * 2;
        //在区块链网络中自动搜寻新的区块的间隔时间。
        public static final long SEARCH_BLOCKS_TIME_INTERVAL = 1000 * 60 * 2;
        //区块高度广播时间间隔
        public static final long BLOCKCHAIN_HEIGHT_BROADCASTER_TIME_INTERVAL = 1000 * 20;
        //区块广播时间间隔。
        public static final long BLOCK_BROADCASTER_TIME_INTERVAL = 1000 * 20;
        //定时将种子节点加入本地区块链网络的时间间隔。
        public static final long ADD_SEED_NODE_TIME_INTERVAL = 1000 * 60 * 2;
        //广播自己节点的时间间隔。
        public static final long NODE_BROADCAST_TIME_INTERVAL = 1000 * 60 * 2;
    }
}
