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
     */
    public abstract void start() throws Exception ;

    //region 监听器
    public abstract void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener) ;

    public abstract void notifyBlockChainActionListener(List<BlockChainActionData> dataList) ;

    public abstract List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) ;

    public abstract List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) ;
    //endregion
}