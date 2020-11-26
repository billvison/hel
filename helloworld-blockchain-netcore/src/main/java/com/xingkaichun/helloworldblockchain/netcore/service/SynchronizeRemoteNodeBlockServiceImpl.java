package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockHashByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SynchronizeRemoteNodeBlockServiceImpl implements SynchronizeRemoteNodeBlockService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeRemoteNodeBlockServiceImpl.class);

    private BlockchainCore blockchainCore;
    private BlockchainCore slaveBlockchainCore;
    private NodeService nodeService;
    private BlockchainNodeClient blockchainNodeClient;
    private ConfigurationService configurationService;


    public SynchronizeRemoteNodeBlockServiceImpl(BlockchainCore blockchainCore, BlockchainCore slaveBlockchainCore, NodeService nodeService, BlockchainNodeClient blockchainNodeClient, ConfigurationService configurationService) {
        this.blockchainCore = blockchainCore;
        this.slaveBlockchainCore = slaveBlockchainCore;
        this.nodeService = nodeService;
        this.blockchainNodeClient = blockchainNodeClient;
        this.configurationService = configurationService;
    }



    @Override
    public void synchronizeRemoteNodeBlock(NodeDto node) {
        BlockchainDatabase blockchainDataBase = blockchainCore.getBlockchainDataBase();

        String nodeId = buildNodeId(node);
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
                    forkNodeHandler(node);
                    return;
                }
                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,forkBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
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
                    break;
                }
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,forkBlockHeight);
                if(blockDTO == null){
                    break;
                }
                //TODO
                Block block = null;//Dto2ModelTool.blockDto2Block(slaveBlockchainCore,blockDTO);
                boolean isAddBlockSuccess = slaveBlockchainCore.addBlock(block);
                if(!isAddBlockSuccess){
                    return;
                }
                forkBlockHeight = forkBlockHeight + LongUtil.ONE;
                //若是有分叉时，一次同步的最后一个区块至少要比本地区块链的高度大于N个
                if(LongUtil.isGreatEqualThan(forkBlockHeight,localBlockchainHeight + GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)){
                    break;
                }
            }
        } else {
            //未分叉
            long tempBlockHeight = localBlockchainHeight + LongUtil.ONE;
            while (true){
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
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

    /**
     * 这里表明真的分叉区块个数过多了，形成了新的分叉，区块链协议不支持同步了。
     */
    private void forkNodeHandler(NodeDto node) {
        nodeService.updateOrInsertForkPropertity(node);
    }

    private String buildNodeId(NodeDto node) {
        return node.getIp();
    }

    private BlockDTO getBlockDtoByBlockHeight(NodeDto node, long blockHeight) {
        ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClient.queryBlockDtoByBlockHeight(node,blockHeight);
        if(!ServiceResult.isSuccess(blockDtoServiceResult)){
            return null;
        }
        BlockDTO blockDTO = blockDtoServiceResult.getResult().getBlockDTO();
        return blockDTO;
    }
}
