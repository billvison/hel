package com.xingkaichun.blockchain.core;

/**
 * 区块链同步器。
 * 区块链是一个分布式的数据库。
 * 本地节点的区块链高度落后于网络节点A的区块链高度，这时就需要将A的区块同步至本地区块链，这个就是该类的主要功能。
 */
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
