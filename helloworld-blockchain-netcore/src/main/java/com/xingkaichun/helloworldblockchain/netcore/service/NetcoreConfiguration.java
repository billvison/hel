package com.xingkaichun.helloworldblockchain.netcore.service;


/**
 * Core配置: NetBlockchainCore的配置。
 * 该类对NetBlockchainCore模块的配置进行统一管理。
 * 在这里可以持久化配置信息。
 * 理论上，NetBlockchainCore模块的任何地方需要配置参数，都可以从该类获取。
 *
 * @author 邢开春 409060350@qq.com
 */
public interface NetcoreConfiguration {
    String getNetcorePath();

    boolean isSynchronizerActive();
    void activeSynchronizer() ;
    void deactiveSynchronizer() ;

    boolean isAutoSearchNode();
    void setAutoSearchNode(boolean autoSearchNode);

    long getSearchNodeTimeInterval();
    long getSearchBlockchainHeightTimeInterval();
    long getSearchBlockTimeInterval();
    long getBlockchainHeightBroadcastTimeInterval();
    long getBlockBroadcastTimeInterval();
    long getAddSeedNodeTimeInterval();
    long getNodeBroadcastTimeInterval();

}
