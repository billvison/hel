package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.BlockChainSynchronizer;
import com.xingkaichun.blockchain.core.ForMinerSynchronizeNodeDataBase;
import com.xingkaichun.blockchain.core.model.Block;

public class BlockChainSynchronizerDefaultImpl implements BlockChainSynchronizer {

    ForMinerSynchronizeNodeDataBase forMinerSynchronizeNodeDataBase;

    //同步其它节点的区块数据:默认同步其它节点区块数据
    private boolean synchronizeBlockChainNodeOption = true;


    @Override
    public void synchronizeBlockChainNode() throws Exception {
        while (synchronizeBlockChainNodeOption){
            String availableSynchronizeNodeId = forMinerSynchronizeNodeDataBase.getDataTransferFinishFlagNodeId();
            if(availableSynchronizeNodeId == null){
                return;
            }
            synchronizeBlockChainNode(availableSynchronizeNodeId);
        }
    }

    private void synchronizeBlockChainNode(String availableSynchronizeNodeId) throws Exception {
        adjustMasterSlaveBlockChainDataBase();
        boolean hasDataTransferFinishFlag = forMinerSynchronizeNodeDataBase.hasDataTransferFinishFlag(availableSynchronizeNodeId);
        if(!hasDataTransferFinishFlag){
            return;
        }
        Block block = forMinerSynchronizeNodeDataBase.getNextBlock(availableSynchronizeNodeId);
        if(block != null){
            reduceBlockChain(blockChainDataBaseSlave,block.getHeight()-1);
            while(true){
                boolean isBlockApplyToBlockChain = isBlockCanApplyToBlockChain(blockChainDataBaseSlave,block);
                if(isBlockApplyToBlockChain){
                    blockChainDataBaseSlave.addBlock(block);
                }else {
                    break;
                }
                block = forMinerSynchronizeNodeDataBase.getNextBlock(availableSynchronizeNodeId);
                if(block == null){
                    break;
                }
            }
        }
        forMinerSynchronizeNodeDataBase.deleteTransferData(availableSynchronizeNodeId);
        forMinerSynchronizeNodeDataBase.clearDataTransferFinishFlag(availableSynchronizeNodeId);
        adjustMasterSlaveBlockChainDataBase();
    }

    private void reduceBlockChain(BlockChainDataBase blockChainDataBase, int blockHeight) throws Exception {
        Block tailBlock = blockChainDataBase.findTailBlock();
        if(tailBlock == null){
            return;
        }
        int currentBlockHeight = tailBlock.getHeight();
        while(currentBlockHeight > blockHeight){
            blockChainDataBase.removeTailBlock();
            tailBlock = blockChainDataBase.findTailBlock();
            if(tailBlock == null){
                return;
            }
            currentBlockHeight = tailBlock.getHeight();
        }
    }
}
