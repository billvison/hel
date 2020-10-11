package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDataBase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.synchronizer.SynchronizerBlockDTO;
import com.xingkaichun.helloworldblockchain.core.tools.NodeTransportDtoTool;
import com.xingkaichun.helloworldblockchain.core.utils.LongUtil;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockHashByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SynchronizeRemoteNodeBlockServiceImpl implements SynchronizeRemoteNodeBlockService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeRemoteNodeBlockServiceImpl.class);

    private BlockChainCore blockChainCore;
    private NodeService nodeService;
    private BlockchainNodeClientService blockchainNodeClientService;
    private ConfigurationService configurationService;
    /**
     * 若是有分叉时，一次同步的最后一个区块的高度至少要比本地区块链的高度大于N个。
     * 假设本地区块链挖矿过快，等外部区块通过网络传输到本节点后，在加入本地区块链做校验的时候，
     * 可能发现同步过来的区块的最大高度已经小于本地区块链的高度了。
     * 所以可以尝试一次多同步几个区块，至少保证本地计算能力达不到同步过来的高度。
     */
    private static final long SYNCHRONIZE_BLOCK_SIZE_FROM_LOCAL_BLOCKCHAIN_HEIGHT = 10;


    public SynchronizeRemoteNodeBlockServiceImpl(BlockChainCore blockChainCore, NodeService nodeService, BlockchainNodeClientService blockchainNodeClientService, ConfigurationService configurationService) {
        this.blockChainCore = blockChainCore;
        this.nodeService = nodeService;
        this.blockchainNodeClientService = blockchainNodeClientService;
        this.configurationService = configurationService;
    }



    @Override
    public void synchronizeRemoteNodeBlock(NodeDto node) {
        if(!isBlockChainIdRight(node)){
            nodeService.deleteNode(node);
        }
        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        Synchronizer synchronizer = blockChainCore.getSynchronizer();
        SynchronizerDataBase synchronizerDataBase = synchronizer.getSynchronizerDataBase();

        String nodeId = buildNodeId(node);
        //这里直接清除老旧的数据，这里希望同步的操作可以在进程没有退出之前完成。
        synchronizerDataBase.clear(nodeId);
        //分叉参数
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.FORK_BLOCK_SIZE.name());
        long forkBlockSize = Long.valueOf(configurationDto.getConfValue());
        Block tailBlock = blockChainDataBase.queryTailBlock();
        long localBlockChainHeight = tailBlock==null? LongUtil.ZERO:tailBlock.getHeight();

        boolean fork = false;
        if(LongUtil.isEquals(localBlockChainHeight,LongUtil.ZERO)){
            fork = false;
        } else {
            localBlockChainHeight = tailBlock.getHeight();
            ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,localBlockChainHeight);
            if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                return;
            }
            String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
            //远程节点的高度没有本地大
            if(blockHash == null || "".equals(blockHash)){
                return;
            } else {
                //没有分叉
                if(tailBlock.getHash().equals(blockHash)){
                    fork = false;
                } else {
                    //有分叉
                    fork = true;
                }
            }
        }

        //确定开始同步高度
        if(fork){
            //从当前区块同步至到未分叉区块
            long tempBlockHeight = localBlockChainHeight;
            while (true){
                if(LongUtil.isLessEqualThan(tempBlockHeight,LongUtil.ZERO)){
                    break;
                }
                if(LongUtil.isGreatThan(localBlockChainHeight,tempBlockHeight+forkBlockSize)){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                SynchronizerBlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
                Block localBlock = blockChainDataBase.queryBlockByBlockHeight(tempBlockHeight);
                if(localBlock.getHash().equals(blockHash)){
                    break;
                }
                synchronizerDataBase.addBlockDTO(nodeId,blockDTO);
                tempBlockHeight = tempBlockHeight - LongUtil.ONE;
            }
            //从当前节点同步至最新
            tempBlockHeight = localBlockChainHeight + LongUtil.ONE;
            while (true){
                if(LongUtil.isLessEqualThan(tempBlockHeight,LongUtil.ZERO)){
                    break;
                }
                SynchronizerBlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                synchronizerDataBase.addBlockDTO(nodeId,blockDTO);
                tempBlockHeight = tempBlockHeight + LongUtil.ONE;
                //若是有分叉时，一次同步的最后一个区块至少要比本地区块链的高度大于N个
                if(LongUtil.isGreatEqualThan(tempBlockHeight,localBlockChainHeight + SYNCHRONIZE_BLOCK_SIZE_FROM_LOCAL_BLOCKCHAIN_HEIGHT)){
                    break;
                }
            }
        } else {
            //未分叉
            long tempBlockHeight = localBlockChainHeight + LongUtil.ONE;
            while (true){
                SynchronizerBlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                Block block = NodeTransportDtoTool.classCast(blockChainDataBase,blockDTO);
                boolean isAddBlockSuccess = blockChainDataBase.addBlock(block);
                if(!isAddBlockSuccess){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }
                tempBlockHeight = tempBlockHeight + LongUtil.ONE;
            }
        }
        synchronizerDataBase.addDataTransferFinishFlag(nodeId);
        //数据库里nodeid的数据必须清空：清空说明这些数据已经被区块链系统使用
        //最大允许的同步时间
        int maxTimestamp = 60*60*1000;
        //当前花费的同步时间
        int totalTimestamp = 0;
        //检测时间间隔
        long timestamp = 10*1000;
        while (true){
            List<String> allNodeId = synchronizerDataBase.getAllNodeId();
            if(allNodeId == null || !allNodeId.contains(nodeId)){
                break;
            }
            ThreadUtil.sleep(timestamp);
            totalTimestamp += timestamp;
            if(totalTimestamp > maxTimestamp){
                logger.error(String.format("节点[%s:%d]数据太长时间没有被区块链系统使用，请检查原因",node.getIp(),node.getPort()));
                synchronizerDataBase.clear(nodeId);
            }
        }
    }

    /**
     * 区块链ID是否正确
     */
    private boolean isBlockChainIdRight(NodeDto node) {
        String currentBlockChainId = GlobalSetting.BLOCK_CHAIN_ID;
        ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
        if(!ServiceResult.isSuccess(pingResponseServiceResult)){
            return false;
        }
        String blockChainId = pingResponseServiceResult.getResult().getBlockChainId();
        return currentBlockChainId.equals(blockChainId);
    }

    /**
     * 这里表明真的分叉区块个数过多了，形成了新的分叉，区块链协议不支持同步了。
     */
    private void forkNodeHandler(NodeDto node, SynchronizerDataBase synchronizerDataBase) {
        synchronizerDataBase.clear(buildNodeId(node));
        nodeService.addOrUpdateNodeForkPropertity(node);
    }

    private String buildNodeId(NodeDto node) {
        return node.getIp()+":"+node.getPort();
    }

    private SynchronizerBlockDTO getBlockDtoByBlockHeight(NodeDto node, long blockHeight) {
        SynchronizerBlockDTO localBlockDTO = getLocalBlockDtoByBlockHeight(node,blockHeight);
        if(localBlockDTO != null){
            return localBlockDTO;
        }
        ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClientService.queryBlockDtoByBlockHeight(node,blockHeight);
        if(!ServiceResult.isSuccess(blockDtoServiceResult)){
            return null;
        }
        BlockDTO blockDTO = blockDtoServiceResult.getResult().getBlockDTO();
        SynchronizerBlockDTO synchronizerBlockDTO = new SynchronizerBlockDTO();
        synchronizerBlockDTO.setHeight(blockDTO.getNonce());
        synchronizerBlockDTO.setNonce(blockDTO.getNonce());
        synchronizerBlockDTO.setTimestamp(blockDTO.getTimestamp());
        synchronizerBlockDTO.setTransactions(blockDTO.getTransactions());
        return synchronizerBlockDTO;
    }

    private SynchronizerBlockDTO getLocalBlockDtoByBlockHeight(NodeDto node, long blockHeight) {
        SynchronizerDataBase synchronizerDataBase = blockChainCore.getSynchronizer().getSynchronizerDataBase();
        SynchronizerBlockDTO blockDTO = synchronizerDataBase.getBlockDto(buildNodeId(node),blockHeight);
        return blockDTO;
    }
}
