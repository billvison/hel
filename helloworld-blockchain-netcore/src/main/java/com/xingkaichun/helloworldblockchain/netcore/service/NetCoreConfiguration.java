package com.xingkaichun.helloworldblockchain.netcore.service;


/**
 * NetCore配置: BlockchainNetCore的配置。
 * 该类对BlockchainNetCore模块的配置进行统一管理。
 * 在这里可以持久化配置信息。
 * 理论上，BlockchainNetCore模块的任何地方需要配置参数，都可以从该类获取。
 *
 * @author 邢开春 409060350@qq.com
 */
public interface NetCoreConfiguration {

    /**
     * BlockchainNetCore数据存储路径
     */
    String getNetCorePath();

    /**
     * 是否"自动搜索新区块"
     */
    boolean isAutoSearchBlock();
    /**
     * 开启"自动搜索新区块"选项
     */
    void activeAutoSearchBlock() ;
    /**
     * 关闭"自动搜索新区块"选项
     */
    void deactiveAutoSearchBlock() ;

    /**
     * 是否自动搜索节点
     */
    boolean isAutoSearchNode();
    /**
     * 开启自动搜索节点
     */
    void activeAutoSearchNode();
    /**
     * 关闭自动搜索节点
     */
    void deactiveAutoSearchNode();


    long getSearchNodeTimeInterval();
    long getSearchBlockchainHeightTimeInterval();
    long getSearchBlockTimeInterval();
    long getBlockchainHeightBroadcastTimeInterval();
    long getBlockBroadcastTimeInterval();
    long getAddSeedNodeTimeInterval();
    long getNodeBroadcastTimeInterval();

    /**
     * 两个区块链有分叉时，区块差异数量大于这个值，则真的分叉了。
     */
    long getForkBlockCount();

    long getSearchUnconfirmedTransactionsInterval();
}
