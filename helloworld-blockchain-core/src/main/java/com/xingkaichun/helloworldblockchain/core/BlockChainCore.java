package com.xingkaichun.helloworldblockchain.core;

/**
 * 区块链Core，代表一个完成的区块链核心系统。
 * 区块链核心系统，由以下几部分组成：
 * 区块链数据库：用于持久化本地区块链的数据
 * 矿工系统：可以收集交易，挖矿，将新挖取的矿放进区块链数据库
 * 区块链同步器系统：区块链是一个分布式的数据库，同步器可以同步其它节点的区块链数据。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public abstract class BlockChainCore {

    //区块链数据库
    protected BlockChainDataBase blockChainDataBase ;
    //矿工
    protected Miner miner ;
    //区块链同步器
    protected Synchronizer synchronizer;

    /**
     * 启动。激活矿工、区块链同步器。
     */
    public abstract void start();





    //region get set
    public BlockChainDataBase getBlockChainDataBase() {
        return blockChainDataBase;
    }

    public void setBlockChainDataBase(BlockChainDataBase blockChainDataBase) {
        this.blockChainDataBase = blockChainDataBase;
    }

    public Miner getMiner() {
        return miner;
    }

    public void setMiner(Miner miner) {
        this.miner = miner;
    }

    public Synchronizer getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }
    //endregion
}