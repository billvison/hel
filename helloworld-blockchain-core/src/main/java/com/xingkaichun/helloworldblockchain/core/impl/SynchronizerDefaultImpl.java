package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDataBase;
import com.xingkaichun.helloworldblockchain.core.utils.NodeTransportUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.EqualsUtils;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

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
                continue;
            }
            synchronizeBlockChainNode(availableSynchronizeNodeId);
        }
    }

    @Override
    public void deactive() {
        synchronizeOption = false;
    }

    @Override
    public void active() {
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

        BigInteger maxBlockHeight = synchronizerDataBase.getMaxBlockHeight(availableSynchronizeNodeId);
        if(maxBlockHeight == null){
            return;
        }
        BigInteger targetBlockChainHeight = targetBlockChainDataBase.obtainBlockChainHeight();
        if(!BigIntegerUtil.isEquals(targetBlockChainHeight,BigInteger.valueOf(0)) && BigIntegerUtil.isGreateEqualThan(targetBlockChainHeight,maxBlockHeight)){
            synchronizerDataBase.clear(availableSynchronizeNodeId);
            return;
        }

        BigInteger minBlockHeight = synchronizerDataBase.getMinBlockHeight(availableSynchronizeNodeId);
        BlockDTO blockDTO = synchronizerDataBase.getBlockDto(availableSynchronizeNodeId,minBlockHeight);
        if(blockDTO != null){
            temporaryBlockChainDataBase.removeBlocksUtilBlockHeightLessThan(blockDTO.getHeight());
            while(blockDTO != null){
                Block block = NodeTransportUtils.classCast(temporaryBlockChainDataBase,blockDTO);
                boolean isAddBlockToBlockChainSuccess = temporaryBlockChainDataBase.addBlock(block);
                if(!isAddBlockToBlockChainSuccess){
                    break;
                }
                minBlockHeight = minBlockHeight.add(BigInteger.ONE);
                blockDTO = synchronizerDataBase.getBlockDto(availableSynchronizeNodeId,minBlockHeight);
            }
        }
        promoteTargetBlockChainDataBase(targetBlockChainDataBase, temporaryBlockChainDataBase);
        synchronizerDataBase.clear(availableSynchronizeNodeId);
    }

    /**
     * 若targetBlockChainDataBase的高度小于blockChainDataBaseTemporary的高度，
     * 则targetBlockChainDataBase同步blockChainDataBaseTemporary的数据。
     */
    private void promoteTargetBlockChainDataBase(BlockChainDataBase targetBlockChainDataBase,
                                                   BlockChainDataBase temporaryBlockChainDataBase) throws Exception {
        Block targetBlockChainTailBlock = targetBlockChainDataBase.findTailNoTransactionBlock();
        Block temporaryBlockChainTailBlock = temporaryBlockChainDataBase.findTailNoTransactionBlock() ;
        //不需要调整
        if(temporaryBlockChainTailBlock == null){
            return;
        }
        if(targetBlockChainTailBlock == null){
            Block block = temporaryBlockChainDataBase.findBlockByBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            boolean isAddBlockToBlockChainSuccess = targetBlockChainDataBase.addBlock(block);
            if(!isAddBlockToBlockChainSuccess){
                return;
            }
        }
        if(BigIntegerUtil.isGreateEqualThan(targetBlockChainTailBlock.getHeight(),temporaryBlockChainTailBlock.getHeight())){
            return;
        }
        //未分叉区块高度
        BigInteger noForkBlockHeight = targetBlockChainTailBlock.getHeight();
        while (true){
            if(BigIntegerUtil.isLessEqualThan(noForkBlockHeight,BigInteger.valueOf(0))){
                break;
            }
            Block targetBlock = targetBlockChainDataBase.findNoTransactionBlockByBlockHeight(noForkBlockHeight);
            if(targetBlock == null){
                break;
            }
            Block temporaryBlock = temporaryBlockChainDataBase.findNoTransactionBlockByBlockHeight(noForkBlockHeight);
            if(targetBlock.getHash().equals(temporaryBlock.getHash()) && targetBlock.getPreviousHash().equals(temporaryBlock.getPreviousHash())){
                break;
            }
            targetBlockChainDataBase.removeTailBlock();
            noForkBlockHeight = targetBlockChainDataBase.obtainBlockChainHeight();
        }

        BigInteger targetBlockChainHeight = targetBlockChainDataBase.obtainBlockChainHeight() ;
        while(true){
            targetBlockChainHeight = targetBlockChainHeight.add(BigInteger.valueOf(1));
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
        Block targetBlockChainTailBlock = targetBlockChainDataBase.findTailNoTransactionBlock() ;
        Block temporaryBlockChainTailBlock = temporaryBlockChainDataBase.findTailNoTransactionBlock() ;
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
            temporaryBlockChainTailBlock = temporaryBlockChainDataBase.findTailNoTransactionBlock();
        }
        if(temporaryBlockChainTailBlock == null){
            Block block = targetBlockChainDataBase.findBlockByBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            boolean isAddBlockToBlockChainSuccess = temporaryBlockChainDataBase.addBlock(block);
            if(!isAddBlockToBlockChainSuccess){
                return;
            }
        }
        BigInteger temporaryBlockChainHeight = temporaryBlockChainDataBase.obtainBlockChainHeight();
        while(true){
            temporaryBlockChainHeight = temporaryBlockChainHeight.add(BigInteger.valueOf(1));
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
