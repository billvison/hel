package com.xingkaichun.helloworldblockchain.netcore.dto.configuration;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public enum ConfigurationEnum {
    IS_MINER_ACTIVE("true","矿工是否处于激活状态？"),
    IS_SYNCHRONIZER_ACTIVE("true","同步者是否处于激活状态？"),
    MINER_ADDRESS(null,"用户设置的矿工钱包的地址。"),
    DEFAULT_MINER_ACCOUNT(null,"默认矿工账户，当用户没有设置自己的挖矿账户时，默认使用这个矿工账户"),
    AUTO_SEARCH_NODE("true","是否允许节点自动在区块链网络中搜索节点。"),
    FORK_BLOCK_SIZE("100","两个区块链有分叉时，区块差异个数大于这个值，则真的分叉了。"),
    NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD("10","本地节点发现某一个节点错误次数过多，则删除该节点。这个阈值配置。"),
    //TODO 更新节点 更新节点的高度 时间太低，如果是内网会导致，外网连不到内网，所以外网节点不能主动将自己的高度通知到内网节点
    NODE_SEARCH_NEW_NODE_TIME_INTERVAL(String.valueOf(2*60*1000),"在区块链网络中自动搜寻新的节点的间隔时间。"),
    SEARCH_NEW_BLOCKS_TIME_INTERVAL(String.valueOf(1*60*1000),"在区块链网络中自动搜寻新的区块的间隔时间。"),
    CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL(String.valueOf(1*60*1000),"检查自己的区块链高度在区块链网络中是否是最大的高度的时间间隔。"),



    ADD_SEED_NODE_TO_LOCAL_BLOCKCHAIN_TIME_INTERVAL(String.valueOf(1*60*60*1000),"定时将种子节点加入本地区块链网络的时间间隔。"),;

    private String defaultConfValue;
    private String details;
    ConfigurationEnum(String defaultConfValue,String details) {
        this.defaultConfValue = defaultConfValue;
        this.details = details;
    }

    public String getDefaultConfValue() {
        return defaultConfValue;
    }

    public String getDetails() {
        return details;
    }
}
