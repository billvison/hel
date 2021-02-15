package com.xingkaichun.helloworldblockchain.netcore.service;


/**
 * 配置service
 *
 * @author 邢开春
 */
public interface ConfigurationService {

    void restoreMinerConfiguration();
    boolean isMinerActive();
    void activeMiner() ;
    void deactiveMiner() ;

    boolean isSynchronizerActive();
    void activeSynchronizer() ;
    void deactiveSynchronizer() ;

    boolean isAutoSearchNode();
    void setAutoSearchNode(boolean autoSearchNode);
}
