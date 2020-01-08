package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

import java.util.ArrayList;
import java.util.List;

public class XXXX {

    BlockChainDataBase blockChainDataBaseMaster ;
    BlockChainDataBase blockChainDataBaseSlave ;

    Miner miner ;

    //监听区块链上区块的增删动作
    private List<BlockChainActionListener> blockChainActionListenerList = new ArrayList<>();



    public XXXX() throws Exception {
        ForMinerTransactionDataBase forMinerTransactionDataBase = new ForMinerTransactionDataBaseDefaultImpl("");
        ForMinerSynchronizeNodeDataBase forMinerSynchronizeNodeDataBase = new ForMinerSynchronizeNodeDataBaseDefaultImpl("");
        PublicKeyString minerPublicKey = new PublicKeyString("");
        this.blockChainDataBaseMaster = new BlockChainDataBaseDefaultImpl("");
        this.blockChainDataBaseSlave = new BlockChainDataBaseDefaultImpl("");
        this.miner = new MinerDefaultImpl(blockChainDataBaseMaster, this.blockChainDataBaseSlave, forMinerSynchronizeNodeDataBase,forMinerTransactionDataBase,minerPublicKey);
    }

    /**
     * 启动挖矿
     */
    public void startMining() throws Exception {
    }


    //region 监听器
    public void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener){
        blockChainActionListenerList.add(blockChainActionListener);
    }

    public void notifyBlockChainActionListener(List<BlockChainActionData> dataList) {
        for (BlockChainActionListener listener: blockChainActionListenerList) {
            listener.addOrDeleteBlock(dataList);
        }
    }

    public List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData addData = new BlockChainActionData(block,blockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }

    public List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData deleteData = new BlockChainActionData(firstBlockList,firstBlockChainActionEnum);
        dataList.add(deleteData);
        BlockChainActionData addData = new BlockChainActionData(nextBlockList,nextBlockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }
    //endregion
}