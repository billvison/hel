package com.xingkaichun.blockchain.core;

public interface BlockChainSynchronizer {

    //region 同步其它区块链节点的数据
    /**
     * 同步其它区块链节点的数据。
     * @throws Exception
     */
    void synchronizeBlockChainNode() throws Exception ;
    /**
     * 暂停同步其它区块链节点的数据
     */
    void pauseSynchronizeBlockChainNode() throws Exception ;
    /**
     * 恢复同步其它区块链节点的数据
     */
    void resumeSynchronizeBlockChainNode() throws Exception;
    //endregion
}
