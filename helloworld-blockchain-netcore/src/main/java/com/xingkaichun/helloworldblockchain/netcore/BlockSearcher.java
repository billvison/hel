package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockHashByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 区块搜索者
 * 1.主动搜索是否存在新的区块
 * 2.如果发现区块链网络中有可以进行同步的区块，则尝试同步区块放入本地区块链。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockSearcher {

    private static final Logger logger = LoggerFactory.getLogger(BlockSearcher.class);

    private ConfigurationService configurationService;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;
    private BlockchainCore slaveBlockchainCore;
    private BlockchainNodeClient blockchainNodeClient;


    public BlockSearcher(ConfigurationService configurationService, NodeService nodeService
            , BlockchainCore blockchainCore
            , BlockchainCore slaveBlockchainCore
            , BlockchainNodeClient blockchainNodeClient) {
        this.configurationService = configurationService;
        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
        this.slaveBlockchainCore = slaveBlockchainCore;
        this.blockchainNodeClient = blockchainNodeClient;
    }

    public void start() {
        /*
         * 搜索其它节点的区块高度
         */
        new Thread(()->{
            while (true){
                try {
                    searchBlocks();
                } catch (Exception e) {
                    logger.error("搜索其它节点的区块高度出现异常",e);
                }
                ThreadUtil.sleep(GlobalSetting.NodeConstant.BLOCK_SEARCH_TIME_INTERVAL);
            }
        }).start();

        /*
         * 同步区块
         */
        new Thread(()->{
            while (true){
                try {
                    if(configurationService.isSynchronizerActive()){
                        synchronizeBlocks();
                    }
                } catch (Exception e) {
                    logger.error("在区块链网络中同步其它节点的区块出现异常",e);
                }
                ThreadUtil.sleep(GlobalSetting.NodeConstant.SEARCH_NEW_BLOCKS_TIME_INTERVAL);
            }
        }).start();
    }

    /**
     * 搜索新的区块，并同步这些区块到本地区块链系统
     */
    private void synchronizeBlocks() {
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        long localBlockchainHeight = blockchainCore.queryBlockchainHeight();
        //可能存在多个节点的数据都比本地节点的区块多，但它们节点的数据可能是相同的，不应该向每个节点都去请求数据。
        for(NodeDto node:nodes){
            if(LongUtil.isLessThan(localBlockchainHeight,node.getBlockchainHeight())){
                //提高主区块链核心的高度
                promoteMasterBlockchainCore(blockchainCore, slaveBlockchainCore);
                //同步主区块链核心数据到从区块链核心
                copyMasterBlockchainCoreToTemporaryBlockchainDataBase(blockchainCore, slaveBlockchainCore);
                //同步节点的区块到从区块链核心
                synchronizeRemoteNodeBlock(blockchainCore,slaveBlockchainCore,nodeService,blockchainNodeClient,node);
                //提高主区块链核心的高度
                promoteMasterBlockchainCore(blockchainCore, slaveBlockchainCore);

                //同步之后，本地区块链高度已经发生改变了
                localBlockchainHeight = blockchainCore.queryBlockchainHeight();
            }
        }
    }


    /**
     * 在区块链网络中搜寻新的区块
     */
    private void searchBlocks() {
        List<NodeDto> nodes = nodeService.queryAllNoForkNodeList();
        for(NodeDto node:nodes){
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClient.pingNode(node);
            boolean isPingSuccess = ServiceResult.isSuccess(pingResponseServiceResult);
            node.setIsNodeAvailable(isPingSuccess);
            if(isPingSuccess){
                PingResponse pingResponse = pingResponseServiceResult.getResult();
                node.setBlockchainHeight(pingResponse.getBlockchainHeight());
                node.setErrorConnectionTimes(0);
                nodeService.updateNode(node);
            } else {
                nodeService.nodeConnectionErrorHandle(node);
            }
        }
    }



    /**
     * 使得temporaryBlockchainDataBase和masterBlockchainCore的区块链数据一模一样
     * @param blockchainCore
     * @param slaveBlockchainCore
     */
    private void copyMasterBlockchainCoreToTemporaryBlockchainDataBase(BlockchainCore blockchainCore,
                                                                           BlockchainCore slaveBlockchainCore) {
        Block targetBlockchainTailBlock = blockchainCore.queryTailBlock() ;
        Block temporaryBlockchainTailBlock = slaveBlockchainCore.queryTailBlock() ;
        if(targetBlockchainTailBlock == null){
            //清空temporary
            slaveBlockchainCore.deleteBlocksUtilBlockHeightLessThan(LongUtil.ONE);
            return;
        }
        //删除Temporary区块链直到尚未分叉位置停止
        while(true){
            if(temporaryBlockchainTailBlock == null){
                break;
            }
            Block targetBlockchainBlock = blockchainCore.queryBlockByBlockHeight(temporaryBlockchainTailBlock.getHeight());
            if(BlockTool.isBlockEquals(targetBlockchainBlock,temporaryBlockchainTailBlock)){
                break;
            }
            slaveBlockchainCore.deleteTailBlock();
            temporaryBlockchainTailBlock = slaveBlockchainCore.queryTailBlock();
        }
        //复制target数据至temporary
        long temporaryBlockchainHeight = slaveBlockchainCore.queryBlockchainHeight();
        while(true){
            temporaryBlockchainHeight++;
            Block currentBlock = blockchainCore.queryBlockByBlockHeight(temporaryBlockchainHeight) ;
            if(currentBlock == null){
                break;
            }
            boolean isAddBlockToBlockchainSuccess = slaveBlockchainCore.addBlock(currentBlock);
            if(!isAddBlockToBlockchainSuccess){
                return;
            }
        }
    }


    /**
     * 若masterBlockchainCore的高度小于slaveBlockchainCore的高度，
     * 则masterBlockchainCore同步slaveBlockchainCore的数据。
     * @param blockchainCore
     * @param slaveBlockchainCore
     */
    private void promoteMasterBlockchainCore(BlockchainCore blockchainCore,
                                                 BlockchainCore slaveBlockchainCore) {
        Block targetBlockchainTailBlock = blockchainCore.queryTailBlock();
        Block temporaryBlockchainTailBlock = slaveBlockchainCore.queryTailBlock() ;
        //不需要调整
        if(temporaryBlockchainTailBlock == null){
            return;
        }
        if(targetBlockchainTailBlock == null){
            Block block = slaveBlockchainCore.queryBlockByBlockHeight(GlobalSetting.GenesisBlock.HEIGHT +1);
            boolean isAddBlockToBlockchainSuccess = blockchainCore.addBlock(block);
            if(!isAddBlockToBlockchainSuccess){
                return;
            }
            targetBlockchainTailBlock = blockchainCore.queryTailBlock();
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
            Block targetBlock = blockchainCore.queryBlockByBlockHeight(noForkBlockHeight);
            if(targetBlock == null){
                break;
            }
            Block temporaryBlock = slaveBlockchainCore.queryBlockByBlockHeight(noForkBlockHeight);
            if(StringUtil.isEquals(targetBlock.getHash(),temporaryBlock.getHash()) &&
                    StringUtil.isEquals(targetBlock.getPreviousBlockHash(),temporaryBlock.getPreviousBlockHash())){
                break;
            }
            blockchainCore.deleteTailBlock();
            noForkBlockHeight = blockchainCore.queryBlockchainHeight();
        }

        long targetBlockchainHeight = blockchainCore.queryBlockchainHeight() ;
        while(true){
            targetBlockchainHeight++;
            Block currentBlock = slaveBlockchainCore.queryBlockByBlockHeight(targetBlockchainHeight) ;
            if(currentBlock == null){
                break;
            }
            boolean isAddBlockToBlockchainSuccess = blockchainCore.addBlock(currentBlock);
            if(!isAddBlockToBlockchainSuccess){
                break;
            }
        }
    }


    public void synchronizeRemoteNodeBlock(BlockchainCore blockchainCore, BlockchainCore slaveBlockchainCore, NodeService nodeService, BlockchainNodeClient blockchainNodeClient, NodeDto node) {
        BlockchainDatabase blockchainDataBase = blockchainCore.getBlockchainDataBase();

        Block tailBlock = blockchainDataBase.queryTailBlock();
        long localBlockchainHeight = tailBlock==null? LongUtil.ZERO:tailBlock.getHeight();

        //本地区块链与node区块链是否分叉？
        boolean fork = false;
        if(LongUtil.isEquals(localBlockchainHeight,LongUtil.ZERO)){
            fork = false;
        } else {
            localBlockchainHeight = tailBlock.getHeight();
            ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,localBlockchainHeight);
            if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                return;
            }
            String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
            //远程节点的高度没有本地大
            if(StringUtil.isNullOrEmpty(blockHash)){
                return;
            } else {
                //没有分叉
                if(StringUtil.isEquals(tailBlock.getHash(),blockHash)){
                    fork = false;
                } else {
                    //有分叉
                    fork = true;
                }
            }
        }

        if(fork){
            //分叉
            //分叉的高度
            long forkBlockHeight = localBlockchainHeight;
            while (true) {
                if (LongUtil.isLessEqualThan(forkBlockHeight, GlobalSetting.GenesisBlock.HEIGHT)) {
                    break;
                }
                //分叉长度过大，不可同步。这里，认为这已经形成了硬分叉(两条完全不同的区块链)。
                if (LongUtil.isGreatThan(localBlockchainHeight, forkBlockHeight + GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)) {
                    nodeService.setNodeFork(node);
                    return;
                }
                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,forkBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    return;
                }
                String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
                Block localBlock = slaveBlockchainCore.queryBlockByBlockHeight(forkBlockHeight);
                if(StringUtil.isEquals(blockHash,localBlock.getHash())){
                    break;
                }
                forkBlockHeight = forkBlockHeight - LongUtil.ONE;
            }
            //从分叉高度开始同步
            slaveBlockchainCore.deleteBlocksUtilBlockHeightLessThan(forkBlockHeight);
            while (true){
                if(LongUtil.isLessEqualThan(forkBlockHeight, GlobalSetting.GenesisBlock.HEIGHT)){
                    return;
                }
                ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClient.queryBlockDtoByBlockHeight(node,forkBlockHeight);
                if(!ServiceResult.isSuccess(blockDtoServiceResult)){
                    return;
                }
                BlockDTO blockDTO = blockDtoServiceResult.getResult().getBlockDTO();
                if(blockDTO == null){
                    return;
                }
                Block block = Dto2ModelTool.blockDto2Block(slaveBlockchainCore.getBlockchainDataBase(),blockDTO);
                boolean isAddBlockSuccess = slaveBlockchainCore.addBlock(block);
                if(!isAddBlockSuccess){
                    return;
                }
                forkBlockHeight = forkBlockHeight + LongUtil.ONE;

                //若是有分叉时，一次同步的最后一个区块至少要比本地区块链的高度大于N个
                if(LongUtil.isGreatEqualThan(forkBlockHeight,localBlockchainHeight + GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)){
                    return;
                }
            }
        } else {
            //未分叉
            long tempBlockHeight = localBlockchainHeight + LongUtil.ONE;
            while (true){
                ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClient.queryBlockDtoByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(blockDtoServiceResult)){
                    return;
                }
                BlockDTO blockDTO = blockDtoServiceResult.getResult().getBlockDTO();
                if(blockDTO == null){
                    return;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    return;
                }

                Block block = Dto2ModelTool.blockDto2Block(blockchainDataBase,blockDTO);
                boolean isAddBlockSuccess = blockchainDataBase.addBlock(block);
                if(!isAddBlockSuccess){
                    return;
                }
                tempBlockHeight = tempBlockHeight + LongUtil.ONE;
            }
        }
    }
}
