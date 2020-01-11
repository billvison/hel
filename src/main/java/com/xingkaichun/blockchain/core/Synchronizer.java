package com.xingkaichun.blockchain.core;

public interface Synchronizer {

    //region 同步其它区块链节点的数据
    /**
     * 开始同步其它区块链节点的数据。
     */
    void run() throws Exception ;
    /**
     * 暂停同步其它区块链节点的数据
     */
    void pause() throws Exception ;
    /**
     * 恢复同步其它区块链节点的数据
     */
    void resume() throws Exception;
    /**
     * 同步功能是否开启
     */
    boolean isActive() throws Exception;
    //endregion
}
