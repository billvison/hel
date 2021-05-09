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
     * 是否同步区块
     */
    boolean isSynchronizerActive();
    /**
     * 激活"同步区块"选项
     */
    void activeSynchronizer() ;
    /**
     * 关闭"同步区块"选项
     */
    void deactiveSynchronizer() ;

    /**
     * 是否自动搜索节点
     */
    boolean isAutoSearchNode();
    /**
     * 设置"是否自动搜索节点"
     */
    void setAutoSearchNode(boolean autoSearchNode);


    long getSearchNodeTimeInterval();
    long getSearchBlockchainHeightTimeInterval();
    long getSearchBlockTimeInterval();
    long getBlockchainHeightBroadcastTimeInterval();
    long getBlockBroadcastTimeInterval();
    long getAddSeedNodeTimeInterval();
    long getNodeBroadcastTimeInterval();

}
