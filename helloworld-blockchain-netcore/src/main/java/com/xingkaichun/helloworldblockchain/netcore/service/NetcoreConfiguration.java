package com.xingkaichun.helloworldblockchain.netcore.service;


/**
 * 配置
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
