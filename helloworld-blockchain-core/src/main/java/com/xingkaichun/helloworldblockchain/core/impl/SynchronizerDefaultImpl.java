package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认实现
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SynchronizerDefaultImpl extends Synchronizer {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizerDefaultImpl.class);

    //本节点的区块链，同步器的目标就是让本节点区块链增长长度。
    private BlockchainDatabase targetBlockchainDatabase;
    /**
     * 一个临时的区块链
     * 同步器实现的机制：
     * ①将本节点的区块链的数据复制进临时区块链
     * ②发现一个可以同步的节点，将这个节点的数据同步至临时区块链
     * ③将临时区块链的数据同步至本节点区块链
     */
    private BlockchainDatabase temporaryBlockchainDatabase;

    //同步开关:默认同步其它节点区块链数据
    private boolean synchronizeOption = true;

    public SynchronizerDefaultImpl(BlockchainDatabase targetBlockchainDatabase,
                                   BlockchainDatabase temporaryBlockchainDatabase,
                                   SynchronizerDatabase synchronizerDataBase) {
        super(synchronizerDataBase);
        this.targetBlockchainDatabase = targetBlockchainDatabase;
        this.temporaryBlockchainDatabase = temporaryBlockchainDatabase;
    }

    @Override
    public void start() {
        while (true){
            ThreadUtil.sleep(10);
            if(!synchronizeOption){
                continue;
            }
            String availableSynchronizeNodeId = synchronizerDataBase.getDataTransferFinishFlagNodeId();
            if(availableSynchronizeNodeId == null){
                continue;
            }
            synchronizeBlockchainNode(availableSynchronizeNodeId);
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

    private void synchronizeBlockchainNode(String availableSynchronizeNodeId) {
        if(!synchronizeOption){
            return;
        }
        copyTargetBlockchainDataBaseToTemporaryBlockchainDataBase(targetBlockchainDatabase, temporaryBlockchainDatabase);
        boolean hasDataTransferFinishFlag = synchronizerDataBase.hasDataTransferFinishFlag(availableSynchronizeNodeId);
        if(!hasDataTransferFinishFlag){
            synchronizerDataBase.clear(availableSynchronizeNodeId);
            return;
        }

        long maxBlockHeight = synchronizerDataBase.getMaxBlockHeight(availableSynchronizeNodeId);
        if(maxBlockHeight <= 0){
            return;
        }
        long targetBlockchainHeight = targetBlockchainDatabase.queryBlockchainHeight();
        if(!LongUtil.isEquals(targetBlockchainHeight,LongUtil.ZERO) && LongUtil.isGreatEqualThan(targetBlockchainHeight,maxBlockHeight)){
            synchronizerDataBase.clear(availableSynchronizeNodeId);
            return;
        }

        long minBlockHeight = synchronizerDataBase.getMinBlockHeight(availableSynchronizeNodeId);
        BlockDTO blockDTO = synchronizerDataBase.getBlockDto(availableSynchronizeNodeId,minBlockHeight);
        if(blockDTO != null){
            temporaryBlockchainDatabase.deleteBlocksUtilBlockHeightLessThan(minBlockHeight);
            while(blockDTO != null){
                Block block = Dto2ModelTool.blockDto2Block(temporaryBlockchainDatabase,blockDTO);
                boolean isAddBlockToBlockchainSuccess = temporaryBlockchainDatabase.addBlock(block);
                if(!isAddBlockToBlockchainSuccess){
                    break;
                }
                minBlockHeight++;
                blockDTO = synchronizerDataBase.getBlockDto(availableSynchronizeNodeId,minBlockHeight);
            }
        }
        promoteTargetBlockchainDataBase(targetBlockchainDatabase, temporaryBlockchainDatabase);
        synchronizerDataBase.clear(availableSynchronizeNodeId);
    }

    /**
     * 若targetBlockchainDataBase的高度小于blockchainDataBaseTemporary的高度，
     * 则targetBlockchainDataBase同步blockchainDataBaseTemporary的数据。
     */
    private void promoteTargetBlockchainDataBase(BlockchainDatabase targetBlockchainDatabase,
                                                 BlockchainDatabase temporaryBlockchainDatabase) {
        Block targetBlockchainTailBlock = targetBlockchainDatabase.queryTailBlock();
        Block temporaryBlockchainTailBlock = temporaryBlockchainDatabase.queryTailBlock() ;
        //不需要调整
        if(temporaryBlockchainTailBlock == null){
            return;
        }
        if(targetBlockchainTailBlock == null){
            Block block = temporaryBlockchainDatabase.queryBlockByBlockHeight(GlobalSetting.GenesisBlock.HEIGHT +1);
            boolean isAddBlockToBlockchainSuccess = targetBlockchainDatabase.addBlock(block);
            if(!isAddBlockToBlockchainSuccess){
                return;
            }
            targetBlockchainTailBlock = targetBlockchainDatabase.queryTailBlock();
        }
        if(targetBlockchainTailBlock == null){
            throw new RuntimeException("在这个时刻，targetBlockchainTailBlock必定不为null。");
        }
        if(LongUtil.isGreatEqualThan(targetBlockchainTailBlock.getHeight(),temporaryBlockchainTailBlock.getHeight())){
            return;
        }
        //未分叉区块高度
        long noForkBlockHeight = targetBlockchainTailBlock.getHeight();
        while (true){
            if(LongUtil.isLessEqualThan(noForkBlockHeight,LongUtil.ZERO)){
                break;
            }
            Block targetBlock = targetBlockchainDatabase.queryBlockByBlockHeight(noForkBlockHeight);
            if(targetBlock == null){
                break;
            }
            Block temporaryBlock = temporaryBlockchainDatabase.queryBlockByBlockHeight(noForkBlockHeight);
            if(StringUtil.isEquals(targetBlock.getHash(),temporaryBlock.getHash()) &&
                    StringUtil.isEquals(targetBlock.getPreviousBlockHash(),temporaryBlock.getPreviousBlockHash())){
                break;
            }
            targetBlockchainDatabase.deleteTailBlock();
            noForkBlockHeight = targetBlockchainDatabase.queryBlockchainHeight();
        }

        long targetBlockchainHeight = targetBlockchainDatabase.queryBlockchainHeight() ;
        while(true){
            targetBlockchainHeight++;
            Block currentBlock = temporaryBlockchainDatabase.queryBlockByBlockHeight(targetBlockchainHeight) ;
            if(currentBlock == null){
                break;
            }
            boolean isAddBlockToBlockchainSuccess = targetBlockchainDatabase.addBlock(currentBlock);
            if(!isAddBlockToBlockchainSuccess){
                break;
            }
        }
    }
    /**
     * 使得temporaryBlockchainDataBase和targetBlockchainDataBase的区块链数据一模一样
     */
    private void copyTargetBlockchainDataBaseToTemporaryBlockchainDataBase(BlockchainDatabase targetBlockchainDatabase,
                                                                           BlockchainDatabase temporaryBlockchainDatabase) {
        Block targetBlockchainTailBlock = targetBlockchainDatabase.queryTailBlock() ;
        Block temporaryBlockchainTailBlock = temporaryBlockchainDatabase.queryTailBlock() ;
        if(targetBlockchainTailBlock == null){
            //清空temporary
            temporaryBlockchainDatabase.deleteBlocksUtilBlockHeightLessThan(LongUtil.ONE);
            return;
        }
        //删除Temporary区块链直到尚未分叉位置停止
        while(true){
            if(temporaryBlockchainTailBlock == null){
                break;
            }
            Block targetBlockchainBlock = targetBlockchainDatabase.queryBlockByBlockHeight(temporaryBlockchainTailBlock.getHeight());
            if(BlockTool.isBlockEquals(targetBlockchainBlock,temporaryBlockchainTailBlock)){
                break;
            }
            temporaryBlockchainDatabase.deleteTailBlock();
            temporaryBlockchainTailBlock = temporaryBlockchainDatabase.queryTailBlock();
        }
        //复制target数据至temporary
        long temporaryBlockchainHeight = temporaryBlockchainDatabase.queryBlockchainHeight();
        while(true){
            temporaryBlockchainHeight++;
            Block currentBlock = targetBlockchainDatabase.queryBlockByBlockHeight(temporaryBlockchainHeight) ;
            if(currentBlock == null){
                break;
            }
            boolean isAddBlockToBlockchainSuccess = temporaryBlockchainDatabase.addBlock(currentBlock);
            if(!isAddBlockToBlockchainSuccess){
                return;
            }
        }
    }
}
