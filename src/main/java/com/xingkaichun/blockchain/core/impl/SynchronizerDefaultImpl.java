package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.Synchronizer;
import com.xingkaichun.blockchain.core.SynchronizerDataBase;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.blockchain.core.utils.atomic.EqualsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizerDefaultImpl extends Synchronizer {

    private Logger logger = LoggerFactory.getLogger(SynchronizerDefaultImpl.class);

    //本节点的区块链，同步器的目标就是让本节点区块链增长长度。
    private BlockChainDataBase targetBlockChainDataBase;
    /**
     * 一个临时的区块链
     * 同步器实现的机制：
     * ①将本节点的区块链的数据复制进临时区块链
     * ②发现一个可以同步的节点，将这个节点的数据同步至临时区块链
     * ③将临时区块链的数据同步至本节点区块链
     */
    private BlockChainDataBase temporaryBlockChainDataBase;

    //同步开关:默认同步其它节点区块链数据
    private boolean synchronizeOption = true;

    public SynchronizerDefaultImpl(BlockChainDataBase targetBlockChainDataBase,
                                   BlockChainDataBase temporaryBlockChainDataBase,
                                   SynchronizerDataBase synchronizerDataBase) {
        this.targetBlockChainDataBase = targetBlockChainDataBase;
        this.temporaryBlockChainDataBase = temporaryBlockChainDataBase;
        this.synchronizerDataBase = synchronizerDataBase;
    }

    @Override
    public void start() throws Exception {
        while (true){
            Thread.sleep(10);
            if(!synchronizeOption){
                continue;
            }
            String availableSynchronizeNodeId = synchronizerDataBase.getDataTransferFinishFlagNodeId();
            if(availableSynchronizeNodeId == null){
                return;
            }
            synchronizeBlockChainNode(availableSynchronizeNodeId);
        }
    }

    @Override
    public void stop() {
        synchronizeOption = false;
    }

    @Override
    public void resume() {
        synchronizeOption = true;
    }

    @Override
    public boolean isActive() {
        return synchronizeOption;
    }

    private void synchronizeBlockChainNode(String availableSynchronizeNodeId) throws Exception {
        if(!synchronizeOption){
            return;
        }
        copyTargetBlockChainDataBaseToTemporaryBlockChainDataBase(targetBlockChainDataBase, temporaryBlockChainDataBase);
        boolean hasDataTransferFinishFlag = synchronizerDataBase.hasDataTransferFinishFlag(availableSynchronizeNodeId);
        if(!hasDataTransferFinishFlag){
            synchronizerDataBase.clear(availableSynchronizeNodeId);
            return;
        }

        int maxBlockHeight = synchronizerDataBase.getMaxBlockHeight(availableSynchronizeNodeId);
        int targetBlockChainHeight = targetBlockChainDataBase.obtainBlockChainHeight();
        if(targetBlockChainHeight != 0 && targetBlockChainHeight >= maxBlockHeight){
            synchronizerDataBase.clear(availableSynchronizeNodeId);
            return;
        }

        Block block = synchronizerDataBase.getNextBlock(availableSynchronizeNodeId);
        if(block != null){
            reduceBlockChain(temporaryBlockChainDataBase,block.getHeight()-1);
            while(block != null){
                boolean isAddBlockToBlockChainSuccess = temporaryBlockChainDataBase.addBlock(block);
                if(!isAddBlockToBlockChainSuccess){
                    break;
                }
                block = synchronizerDataBase.getNextBlock(availableSynchronizeNodeId);
            }
        }
        synchronizerDataBase.clear(availableSynchronizeNodeId);
        promoteTargetBlockChainDataBase(targetBlockChainDataBase, temporaryBlockChainDataBase);
    }

    /**
     * 若targetBlockChainDataBase的高度小于blockChainDataBaseTemporary的高度，
     * 则targetBlockChainDataBase同步blockChainDataBaseTemporary的数据。
     */
    private void promoteTargetBlockChainDataBase(BlockChainDataBase targetBlockChainDataBase,
                                                   BlockChainDataBase temporaryBlockChainDataBase) throws Exception {
        Block targetBlockChainTailBlock = targetBlockChainDataBase.findTailBlock() ;
        Block TemporaryBlockChainTailBlock = temporaryBlockChainDataBase.findTailBlock() ;
        //不需要调整
        if(TemporaryBlockChainTailBlock == null){
            return;
        }
        if(targetBlockChainTailBlock == null){
            Block block = temporaryBlockChainDataBase.findBlockByBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            boolean isAddBlockToBlockChainSuccess = targetBlockChainDataBase.addBlock(block);
            if(!isAddBlockToBlockChainSuccess){
                return;
            }
        }
        int targetBlockChainHeight = targetBlockChainDataBase.findTailBlock().getHeight() ;
        while(true){
            targetBlockChainHeight++;
            Block currentBlock = temporaryBlockChainDataBase.findBlockByBlockHeight(targetBlockChainHeight) ;
            if(currentBlock == null){
                break;
            }
            boolean isAddBlockToBlockChainSuccess = targetBlockChainDataBase.addBlock(currentBlock);
            if(!isAddBlockToBlockChainSuccess){
                break;
            }
        }
    }
    /**
     * 使得temporaryBlockChainDataBase和targetBlockChainDataBase的区块链数据一模一样
     */
    private void copyTargetBlockChainDataBaseToTemporaryBlockChainDataBase(BlockChainDataBase targetBlockChainDataBase,
                                 BlockChainDataBase temporaryBlockChainDataBase) throws Exception {
        Block targetBlockChainTailBlock = targetBlockChainDataBase.findTailBlock() ;
        Block temporaryBlockChainTailBlock = temporaryBlockChainDataBase.findTailBlock() ;
        //不需要调整
        if(targetBlockChainTailBlock == null){
            return;
        }
        //删除Temporary区块链直到尚未分叉位置停止
        while(true){
            if(temporaryBlockChainTailBlock == null){
                break;
            }
            if(isBlockEqual(targetBlockChainTailBlock,temporaryBlockChainTailBlock)){
                break;
            }
            temporaryBlockChainDataBase.removeTailBlock();
            temporaryBlockChainTailBlock = temporaryBlockChainDataBase.findTailBlock() ;
        }
        if(temporaryBlockChainTailBlock == null){
            Block block = targetBlockChainDataBase.findBlockByBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            boolean isAddBlockToBlockChainSuccess = temporaryBlockChainDataBase.addBlock(block);
            if(!isAddBlockToBlockChainSuccess){
                return;
            }
        }
        int temporaryBlockChainHeight = temporaryBlockChainDataBase.obtainBlockChainHeight();
        while(true){
            temporaryBlockChainHeight++;
            Block currentBlock = targetBlockChainDataBase.findBlockByBlockHeight(temporaryBlockChainHeight) ;
            if(currentBlock == null){
                break;
            }
            boolean isAddBlockToBlockChainSuccess = temporaryBlockChainDataBase.addBlock(currentBlock);
            if(!isAddBlockToBlockChainSuccess){
                return;
            }
        }
    }
    /**
     * 降低区块链高度
     * @param blockChainDataBase 区块链
     * @param blockHeight 降低区块链高度到的位置
     */
    private void reduceBlockChain(BlockChainDataBase blockChainDataBase, int blockHeight) throws Exception {
        if(blockHeight < 0){
            return;
        }
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
    private boolean isBlockEqual(Block block1, Block block2) {
        if(block1 == null && block2 == null){
            return true;
        }
        if(block1 == null || block2 == null){
            return false;
        }
        //不严格校验,这里没有具体校验每一笔交易
        if(EqualsUtils.isEquals(block1.getPreviousHash(),block2.getPreviousHash())
                && EqualsUtils.isEquals(block1.getHeight(),block2.getHeight())
                && EqualsUtils.isEquals(block1.getMerkleRoot(),block2.getMerkleRoot())
                && EqualsUtils.isEquals(block1.getNonce(),block2.getNonce())
                && EqualsUtils.isEquals(block1.getHash(),block2.getHash())){
            return true;
        }
        return false;
    }
}
