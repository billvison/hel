package com.xingkaichun.helloworldblockchain.core;

/**
 * 单机版[没有网络交互版本]区块链核心，代表一个完整的单机版区块链核心系统。
 * 单机版区块链核心系统，由以下几部分组成：
 * 区块链数据库：用于持久化本地区块链的数据
 * @see com.xingkaichun.helloworldblockchain.core.BlockChainDataBase
 * 矿工：可以收集交易，挖矿，将新挖取的矿放进区块链数据库
 * @see com.xingkaichun.helloworldblockchain.core.Miner
 * 区块链同步器：区块链是一个分布式的数据库，同步器可以同步其它节点的区块链数据。
 * @see com.xingkaichun.helloworldblockchain.core.Synchronizer
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class BlockChainCore {

    //区块链数据库
    protected BlockChainDataBase blockChainDataBase ;
    //矿工
    protected Miner miner ;
    //区块链同步器
    protected Synchronizer synchronizer ;

    public BlockChainCore(BlockChainDataBase blockChainDataBase, Miner miner, Synchronizer synchronizer) {
        this.blockChainDataBase = blockChainDataBase;
        this.miner = miner;
        this.synchronizer = synchronizer;
    }

    /**
     * 激活区块链核心。激活矿工、激活区块链同步器。
     */
    public abstract void start();





    //region get set
    public BlockChainDataBase getBlockChainDataBase() {
        return blockChainDataBase;
    }

    public Miner getMiner() {
        return miner;
    }

    public Synchronizer getSynchronizer() {
        return synchronizer;
    }
    //endregion
}