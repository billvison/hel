package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
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
                copyMasterBlockchainCoreToSlaveBlockchainCore(blockchainCore, slaveBlockchainCore);
                //同步远程节点的区块
                synchronizeRemoteNodeBlock(blockchainCore,slaveBlockchainCore,nodeService,blockchainNodeClient,node);
                //提高主区块链核心的高度
                promoteMasterBlockchainCore(blockchainCore, slaveBlockchainCore);

                //同步之后，本地区块链高度已经发生改变了
                localBlockchainHeight = blockchainCore.queryBlockchainHeight();
            }
        }
    }

    /**
     * 使得slaveBlockchainCore和masterBlockchainCore的区块链数据一模一样
     * @param masterBlockchainCore
     * @param slaveBlockchainCore
     */
    private void copyMasterBlockchainCoreToSlaveBlockchainCore(BlockchainCore masterBlockchainCore,BlockchainCore slaveBlockchainCore) {
        Block masterBlockchainTailBlock = masterBlockchainCore.queryTailBlock() ;
        Block slaveBlockchainTailBlock = slaveBlockchainCore.queryTailBlock() ;
        if(masterBlockchainTailBlock == null){
            //清空slave
            slaveBlockchainCore.deleteBlocksUtilBlockHeightLessThan(GlobalSetting.GenesisBlock.HEIGHT);
            return;
        }
        //删除slave区块直至slave区块和master区块保持一致
        while(true){
            if(slaveBlockchainTailBlock == null){
                break;
            }
            Block masterBlockchainBlock = masterBlockchainCore.queryBlockByBlockHeight(slaveBlockchainTailBlock.getHeight());
            if(BlockTool.isBlockEquals(masterBlockchainBlock,slaveBlockchainTailBlock)){
                break;
            }
            slaveBlockchainCore.deleteTailBlock();
            slaveBlockchainTailBlock = slaveBlockchainCore.queryTailBlock();
        }
        //复制master数据至slave
        while(true){
            long slaveBlockchainHeight = slaveBlockchainCore.queryBlockchainHeight();
            Block nextBlock = masterBlockchainCore.queryBlockByBlockHeight(slaveBlockchainHeight+1) ;
            if(nextBlock == null){
                break;
            }
            boolean isAddBlockToBlockchainSuccess = slaveBlockchainCore.addBlock(nextBlock);
            if(!isAddBlockToBlockchainSuccess){
                return;
            }
        }
    }


    /**
     * 增加主区块链的区块
     * @param masterBlockchainCore
     * @param slaveBlockchainCore
     */
    private void promoteMasterBlockchainCore(BlockchainCore masterBlockchainCore,
                                                 BlockchainCore slaveBlockchainCore) {
        Block masterBlockchainTailBlock = masterBlockchainCore.queryTailBlock();
        Block slaveBlockchainTailBlock = slaveBlockchainCore.queryTailBlock() ;
        //不需要调整：主区块链的高度一定大于或等于辅区块链的高度
        if(slaveBlockchainTailBlock == null){
            return;
        }
        if(masterBlockchainTailBlock == null){
            Block block = slaveBlockchainCore.queryBlockByBlockHeight(GlobalSetting.GenesisBlock.HEIGHT +1);
            boolean isAddBlockToBlockchainSuccess = masterBlockchainCore.addBlock(block);
            if(!isAddBlockToBlockchainSuccess){
                return;
            }
            masterBlockchainTailBlock = masterBlockchainCore.queryTailBlock();
        }
        if(LongUtil.isGreatEqualThan(masterBlockchainTailBlock.getHeight(),slaveBlockchainTailBlock.getHeight())){
            return;
        }

        //是否硬分叉
        long blockHeight = masterBlockchainTailBlock.getHeight();
        while (true){
            if(LongUtil.isLessEqualThan(blockHeight,GlobalSetting.GenesisBlock.HEIGHT)){
                break;
            }
            Block masterBlock = masterBlockchainCore.queryBlockByBlockHeight(blockHeight);
            Block slaveBlock = slaveBlockchainCore.queryBlockByBlockHeight(blockHeight);
            if(BlockTool.isBlockEquals(masterBlock,slaveBlock)){
                break;
            }
            if(LongUtil.isGreatEqualThan(masterBlockchainTailBlock.getHeight()-blockHeight+1,GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)){
                //硬分叉，终止。
                return;
            }
            blockHeight--;
        }


        //删除主区块链分叉区块
        long masterBlockchainTailBlockHeight = masterBlockchainTailBlock.getHeight();
        while (true){
            if(LongUtil.isLessEqualThan(masterBlockchainTailBlockHeight,GlobalSetting.GenesisBlock.HEIGHT)){
                break;
            }
            Block masterBlock = masterBlockchainCore.queryBlockByBlockHeight(masterBlockchainTailBlockHeight);
            Block slaveBlock = slaveBlockchainCore.queryBlockByBlockHeight(masterBlockchainTailBlockHeight);
            if(StringUtil.isEquals(masterBlock.getHash(),slaveBlock.getHash())){
                break;
            }
            masterBlockchainCore.deleteTailBlock();
            masterBlockchainTailBlockHeight = masterBlockchainCore.queryBlockchainHeight();
        }

        //主区块链增加区块
        while(true){
            masterBlockchainTailBlockHeight = masterBlockchainCore.queryBlockchainHeight();
            Block nextBlock = slaveBlockchainCore.queryBlockByBlockHeight(masterBlockchainTailBlockHeight+1) ;
            if(nextBlock == null){
                break;
            }
            boolean isAddBlockToBlockchainSuccess = masterBlockchainCore.addBlock(nextBlock);
            if(!isAddBlockToBlockchainSuccess){
                break;
            }
        }
    }


    public void synchronizeRemoteNodeBlock(BlockchainCore masterBlockchainCore, BlockchainCore slaveBlockchainCore, NodeService nodeService, BlockchainNodeClient blockchainNodeClient, NodeDto node) {

        Block masterBlockchainCoreTailBlock = masterBlockchainCore.queryTailBlock();
        long masterBlockchainCoreTailBlockHeight = masterBlockchainCore.queryBlockchainHeight();

        //本地区块链与node区块链是否分叉？
        boolean fork = false;
        if(LongUtil.isEquals(masterBlockchainCoreTailBlockHeight,GlobalSetting.GenesisBlock.HEIGHT)){
            fork = false;
        } else {
            ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,masterBlockchainCoreTailBlockHeight);
            if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                return;
            }
            String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
            //没有查询到区块哈希，代表着远程节点的高度没有本地大
            if(StringUtil.isNullOrEmpty(blockHash)){
                return;
            }
            //没有分叉
            if(StringUtil.isEquals(masterBlockchainCoreTailBlock.getHash(),blockHash)){
                fork = false;
            } else {
                //有分叉
                fork = true;
            }
        }

        if(fork){
            //分叉
            //分叉的高度
            long forkBlockHeight = masterBlockchainCoreTailBlockHeight;
            while (true) {
                if (LongUtil.isLessEqualThan(forkBlockHeight, GlobalSetting.GenesisBlock.HEIGHT)) {
                    break;
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
                //分叉长度过大，不可同步。这里，认为这已经形成了硬分叉(两条完全不同的区块链)。
                if (LongUtil.isGreatThan(masterBlockchainCoreTailBlockHeight, forkBlockHeight + GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)) {
                    nodeService.setNodeFork(node);
                    return;
                }
                forkBlockHeight--;
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
                forkBlockHeight++;

                //若是有分叉时，一次同步的最后一个区块至少要比本地区块链的高度大于N个
                if(LongUtil.isGreatEqualThan(forkBlockHeight,masterBlockchainCoreTailBlockHeight + GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)){
                    return;
                }
            }
        } else {
            //未分叉
            while (true){
                masterBlockchainCoreTailBlockHeight = masterBlockchainCore.queryBlockchainHeight();
                ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClient.queryBlockDtoByBlockHeight(node,masterBlockchainCoreTailBlockHeight+1);
                if(!ServiceResult.isSuccess(blockDtoServiceResult)){
                    return;
                }
                BlockDTO blockDTO = blockDtoServiceResult.getResult().getBlockDTO();
                if(blockDTO == null){
                    return;
                }

                Block block = Dto2ModelTool.blockDto2Block(masterBlockchainCore.getBlockchainDataBase(),blockDTO);
                boolean isAddBlockSuccess = masterBlockchainCore.addBlock(block);
                if(!isAddBlockSuccess){
                    return;
                }
            }
        }
    }
}
