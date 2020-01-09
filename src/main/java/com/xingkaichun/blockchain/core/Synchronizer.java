package com.xingkaichun.blockchain.core;

public interface Synchronizer {

    //region 同步其它区块链节点的数据
    /**
     * 同步其它区块链节点的数据。
     * @throws Exception
     */
    void synchronize() throws Exception ;
    /**
     * 暂停同步其它区块链节点的数据
     */
    void pause() throws Exception ;
    /**
     * 恢复同步其它区块链节点的数据
     */
    void resume() throws Exception;

    boolean isActive() throws Exception;
    //endregion
}
