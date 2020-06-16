package com.xingkaichun.helloworldblockchain.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

/**
 * 区块链核心，代表一个完整的区块链核心系统。
 * 区块链核心系统，由以下几部分组成：
 * 区块链数据库：用于持久化本地区块链的数据
 * 矿工：可以收集交易，挖矿，将新挖取的矿放进区块链数据库
 * 区块链同步器：区块链是一个分布式的数据库，同步器可以同步其它节点的区块链数据。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public abstract class BlockChainCore {

    //区块链数据库
    protected BlockChainDataBase blockChainDataBase ;
    //矿工
    protected Miner miner ;
    //区块链同步器
    protected Synchronizer synchronizer ;

    static {
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if(provider == null){
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

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