package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import lombok.Data;

import java.util.List;

/**
 * 区块链Core，代表一个完成的区块链核心系统。
 * 区块链核心系统，由以下几部分组成：
 * 区块链数据库：用于持久化本地区块链的数据
 * 矿工系统：可以收集交易，挖矿，将新挖取的矿放进区块链数据库
 * 区块链同步器系统：区块链是一个分布式的数据库，同步器可以同步其它节点的区块链数据。
 */
@Data
public abstract class BlockChainCore {

    //区块链数据库
    protected BlockChainDataBase blockChainDataBase ;
    //矿工
    protected Miner miner ;
    //区块链同步器
    protected Synchronizer synchronizer;

    //监听区块链上区块的增删动作
    protected List<BlockChainActionListener> blockChainActionListenerList;

    /**
     * 启动。激活矿工、区块链同步器。
     */
    public abstract void start();

    //region 监听器
    /**
     * 注册监听器
     */
    public void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener){
        blockChainActionListenerList.add(blockChainActionListener);
    }

    /**
     * 调用监听器
     */
    public void notifyBlockChainActionListener(List<BlockChainActionData> dataList) {
        for (BlockChainActionListener listener: blockChainActionListenerList) {
            listener.addOrDeleteBlock(dataList);
        }
    }
    //endregion
}