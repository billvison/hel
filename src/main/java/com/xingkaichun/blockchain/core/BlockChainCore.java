package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;
import lombok.Data;

import java.util.List;

@Data
public abstract class BlockChainCore {

    protected Miner miner ;
    protected Synchronizer synchronizer;
    protected Incentive incentive ;
    protected Consensus consensus ;
    protected BlockChainDataBase blockChainDataBase ;
    protected MinerTransactionDtoDataBase minerTransactionDtoDataBase;

    //监听区块链上区块的增删动作
    protected List<BlockChainActionListener> blockChainActionListenerList;

    /**
     * 启动
     * 这里是一个单线程实现。为了协调节点间的区块同步、矿工的挖矿，先进行节点间区块数据的同步，
     * 同步结束后，矿工进行一段时间的挖矿，然后退出挖矿，进行区块同步，矿工进行一段时间的挖矿，
     * 然后退出挖矿，进行区块同步......
     */
    public abstract void run() throws Exception ;

    /**
     * 暂停所有
     */
    public abstract void pause() throws Exception ;

    /**
     * 恢复所有
     */
    public abstract void resume() throws Exception ;

    public abstract boolean isActive() throws Exception ;


    //region 监听器
    public abstract void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener) ;

    public abstract void notifyBlockChainActionListener(List<BlockChainActionData> dataList) ;

    public abstract List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) ;

    public abstract List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) ;
    //endregion
}