package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;
import lombok.Data;

import java.util.List;

@Data
public abstract class BlockChainCore {

    //矿工
    protected Miner miner ;
    //区块链同步器
    protected Synchronizer synchronizer;
    //区块链数据库
    protected BlockChainDataBase blockChainDataBase ;

    //监听区块链上区块的增删动作
    protected List<BlockChainActionListener> blockChainActionListenerList;

    /**
     * 启动。激活矿工、区块链同步器。
     */
    public abstract void start() throws Exception ;

    //region 监听器
    public abstract void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener) ;

    public abstract void notifyBlockChainActionListener(List<BlockChainActionData> dataList) ;

    public abstract List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) ;

    public abstract List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) ;
    //endregion
}