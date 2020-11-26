package com.xingkaichun.helloworldblockchain.netcore.service;


/**
 * 配置service
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface ConfigurationService {

    void restoreMinerConfiguration();
    boolean isMinerActive();
    void activeMiner() ;
    void deactiveMiner() ;

    void restoreSynchronizerConfiguration();
    boolean isSynchronizerActive();
    void activeSynchronizer() ;
    void deactiveSynchronizer() ;//AUTO_SEARCH_NODE

    boolean isAutoSearchNode();
    void setAutoSearchNode(boolean autoSearchNode);
}
