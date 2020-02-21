package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;

import java.util.ArrayList;
import java.util.List;

public class BlockChainCoreImpl extends BlockChainCore {

    public BlockChainCoreImpl() throws Exception {
        this.blockChainActionListenerList = new ArrayList<>();
    }

    /**
     * 启动
     * 这里是一个单线程实现。
     * 因为节点间的数据同步可能更改本地区块链，矿工挖到矿也会更改本地区块链。
     *
     * 这里会为了协调节点间的区块同步、矿工的挖矿，先进行节点间区块数据的同步，
     * 同步结束后，矿工进行一段时间的挖矿，然后退出挖矿，进行区块同步，矿工进行一段时间的挖矿，
     * 然后退出挖矿，进行区块同步......
     */
    @Override
    public void start() throws Exception {
        new Thread(
                ()->{
                    try {
                        synchronizer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
        new Thread(
                ()->{
                    try {
                        miner.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    //region 监听器
    @Override
    public void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener){
        blockChainActionListenerList.add(blockChainActionListener);
    }

    @Override
    public void notifyBlockChainActionListener(List<BlockChainActionData> dataList) {
        for (BlockChainActionListener listener: blockChainActionListenerList) {
            listener.addOrDeleteBlock(dataList);
        }
    }

    @Override
    public List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData addData = new BlockChainActionData(block,blockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }

    @Override
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