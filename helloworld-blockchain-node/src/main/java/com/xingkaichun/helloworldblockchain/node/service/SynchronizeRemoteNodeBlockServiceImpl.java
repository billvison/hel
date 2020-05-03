package com.xingkaichun.helloworldblockchain.node.service;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDataBase;
import com.xingkaichun.helloworldblockchain.core.utils.BlockChainCoreConstant;
import com.xingkaichun.helloworldblockchain.core.utils.NodeTransportDtoUtil;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.node.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.QueryBlockHashByBlockHeightResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Service
public class SynchronizeRemoteNodeBlockServiceImpl implements SynchronizeRemoteNodeBlockService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeRemoteNodeBlockServiceImpl.class);

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockChainBranchService blockChainBranchService;

    @Autowired
    private BlockchainNodeClientService blockchainNodeClientService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private Gson gson;

    /**
     * 若是有分叉时，一次同步的最后一个区块的高度至少要比本地区块链的高度大于N个。
     * 假设本地区块链挖矿过快，等外部区块通过网络传输到本节点后，在加入本地区块链做校验的时候，
     * 可能发现同步过来的区块的最大高度已经小于本地区块链的高度了。
     * 所以可以尝试一次多同步几个区块，至少保证本地计算能力达不到同步过来的高度。
     */
    private BigInteger SYNCHRONIZE_BLOCK_SIZE_FROM_LOCAL_BLOCKCHAIN_HEIGHT = new BigInteger("10");


    @Override
    public void synchronizeRemoteNodeBlock(Node node) throws Exception {
        if(!isBlockChainIdRight(node)){
            nodeService.deleteNode(node);
        }
        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        Synchronizer synchronizer = blockChainCore.getSynchronizer();
        SynchronizerDataBase synchronizerDataBase = synchronizer.getSynchronizerDataBase();

        if(isFork(node)){
            forkNodeHandler(node,synchronizerDataBase);
            return;
        }

        String nodeId = buildNodeId(node);
        //这里直接清除老旧的数据，这里希望同步的操作可以在进程没有退出之前完成。
        synchronizerDataBase.clear(nodeId);
        //分叉参数
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.FORK_BLOCK_SIZE.name());
        BigInteger forkBlockSize = new BigInteger(configurationDto.getConfValue());
        Block tailBlock = blockChainDataBase.findTailNoTransactionBlock();
        BigInteger localBlockChainHeight = tailBlock==null?BigInteger.ZERO:tailBlock.getHeight();

        boolean fork = false;
        if(BigIntegerUtil.isEquals(localBlockChainHeight,BigInteger.ZERO)){
            fork = false;
        } else {
            localBlockChainHeight = tailBlock.getHeight();
            ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,localBlockChainHeight);
            if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                return;
            }
            String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
            if(blockChainBranchService.isFork(localBlockChainHeight,blockHash)){
                forkNodeHandler(node,synchronizerDataBase);
                return;
            }
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
            BigInteger tempBlockHeight = localBlockChainHeight;
            while (true){
                if(BigIntegerUtil.isLessEqualThan(tempBlockHeight,BigInteger.ZERO)){
                    break;
                }
                if(BigIntegerUtil.isGreateThan(localBlockChainHeight,tempBlockHeight.add(forkBlockSize))){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
                if(blockChainBranchService.isFork(tempBlockHeight,blockHash)){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                Block localBlock = blockChainDataBase.findNoTransactionBlockByBlockHeight(tempBlockHeight);
                if(localBlock.getHash().equals(blockHash)){
                    break;
                }
                synchronizerDataBase.addBlockDTO(nodeId,blockDTO);
                tempBlockHeight = tempBlockHeight.subtract(BigInteger.ONE);
            }
            //从当前节点同步至最新
            tempBlockHeight = localBlockChainHeight.add(BigInteger.ONE);
            while (true){
                if(BigIntegerUtil.isLessEqualThan(tempBlockHeight,BigInteger.ZERO)){
                    break;
                }
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();

                if(blockChainBranchService.isFork(tempBlockHeight,blockHash)){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                synchronizerDataBase.addBlockDTO(nodeId,blockDTO);
                tempBlockHeight = tempBlockHeight.add(BigInteger.ONE);
                //若是有分叉时，一次同步的最后一个区块至少要比本地区块链的高度大于N个
                if(BigIntegerUtil.isGreateEqualThan(tempBlockHeight,localBlockChainHeight.add(SYNCHRONIZE_BLOCK_SIZE_FROM_LOCAL_BLOCKCHAIN_HEIGHT))){
                    break;
                }
            }
        } else {
            //未分叉
            BigInteger tempBlockHeight = localBlockChainHeight.add(BigInteger.ONE);
            while (true){
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
                if(blockChainBranchService.isFork(tempBlockHeight,blockHash)){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                Block block = NodeTransportDtoUtil.classCast(blockChainDataBase,blockDTO);
                boolean isAddBlockSuccess = blockChainDataBase.addBlock(block);
                if(!isAddBlockSuccess){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }
                tempBlockHeight = tempBlockHeight.add(BigInteger.ONE);
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
            Thread.sleep(timestamp);
            totalTimestamp += timestamp;
            if(totalTimestamp>maxTimestamp){
                logger.error(String.format("节点[%s:%d]数据太长时间没有被区块链系统使用，请检查原因",node.getIp(),node.getPort()));
                synchronizerDataBase.clear(nodeId);
            }
        }
    }

    /**
     * 区块链ID是否正确
     */
    private boolean isBlockChainIdRight(Node node) {
        String currentBlockChainId = BlockChainCoreConstant.BLOCK_CHAIN_ID;
        ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
        if(!ServiceResult.isSuccess(pingResponseServiceResult)){
            return false;
        }
        String blockChainId = pingResponseServiceResult.getResult().getBlockChainId();
        return currentBlockChainId.equals(blockChainId);
    }

    private boolean isFork(Node node) {
        ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
        if(!ServiceResult.isSuccess(pingResponseServiceResult)){
            return false;
        }
        BigInteger blockChainHeight = pingResponseServiceResult.getResult().getBlockChainHeight();
        if(BigIntegerUtil.isLessEqualThan(blockChainHeight,BigInteger.ZERO)){
            return false;
        }
        BigInteger nearBlockHeight = blockChainBranchService.getFixBlockHashMaxBlockHeight(blockChainHeight);
        if(BigIntegerUtil.isLessEqualThan(nearBlockHeight,BigInteger.ZERO)){
            return false;
        }
        ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClientService.queryBlockHashByBlockHeight(node,nearBlockHeight);
        if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
            return false;
        }
        return blockChainBranchService.isFork(nearBlockHeight,queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash());
    }


    /**
     * 这里表明真的分叉区块个数过多了，形成了新的分叉，区块链协议不支持同步了。
     */
    private void forkNodeHandler(Node node,SynchronizerDataBase synchronizerDataBase) throws Exception {
        synchronizerDataBase.clear(buildNodeId(node));
        nodeService.addOrUpdateNodeForkPropertity(node);
    }

    private String buildNodeId(Node node) {
        return node.getIp()+":"+node.getPort();
    }

    private BlockDTO getBlockDtoByBlockHeight(Node node, BigInteger blockHeight) {
        try {
            BlockDTO localBlockDTO = getLocalBlockDtoByBlockHeight(node,blockHeight);
            if(localBlockDTO != null){
                return localBlockDTO;
            }
            ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClientService.queryBlockDtoByBlockHeight(node,blockHeight);
            if(!ServiceResult.isSuccess(blockDtoServiceResult)){
                return null;
            }
            return blockDtoServiceResult.getResult().getBlockDTO();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BlockDTO getLocalBlockDtoByBlockHeight(Node node, BigInteger blockHeight) throws Exception {
        SynchronizerDataBase synchronizerDataBase = blockChainCore.getSynchronizer().getSynchronizerDataBase();
        BlockDTO blockDTO = synchronizerDataBase.getBlockDto(buildNodeId(node),blockHeight);
        return blockDTO;
    }
}
