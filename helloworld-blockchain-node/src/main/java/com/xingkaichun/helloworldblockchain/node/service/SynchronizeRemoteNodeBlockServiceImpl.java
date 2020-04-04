package com.xingkaichun.helloworldblockchain.node.service;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDataBase;
import com.xingkaichun.helloworldblockchain.core.utils.DtoUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.model.Block;
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
    private Gson gson;

    @Override
    public void synchronizeRemoteNodeBlock(Node node) throws Exception {
        
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
        //最大支持的回滚的区块个数。
        //TODO 配置
        BigInteger rollBlockSizeAllow = BigInteger.valueOf(100);
        //每次支持同步区块数
        //TODO 配置
        BigInteger synchronizationBlockSize = BigInteger.valueOf(100);
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
                if(BigIntegerUtil.isGreateThan(localBlockChainHeight,tempBlockHeight.add(rollBlockSizeAllow))){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }
                if(blockChainBranchService.isFork(tempBlockHeight,blockDTO.getHash())){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                Block localBlock = blockChainDataBase.findNoTransactionBlockByBlockHeight(tempBlockHeight);
                if(localBlock.getHash().equals(blockDTO.getHash())){
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
                if(blockChainBranchService.isFork(tempBlockHeight,blockDTO.getHash())){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                synchronizerDataBase.addBlockDTO(nodeId,blockDTO);
                tempBlockHeight = tempBlockHeight.add(BigInteger.ONE);
                if(BigIntegerUtil.isGreateEqualThan(tempBlockHeight,localBlockChainHeight.add(synchronizationBlockSize))){
                    //一次不要同步过多
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
                if(blockChainBranchService.isFork(tempBlockHeight,blockDTO.getHash())){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                Block block = DtoUtils.classCast(blockChainDataBase,blockDTO);
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

    private boolean isFork(Node node) {
        ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
        if(!ServiceResult.isSuccess(pingResponseServiceResult)){
            return false;
        }
        BigInteger blockChainHeight = pingResponseServiceResult.getResult().getBlockChainHeight();
        if(BigIntegerUtil.isLessEqualThan(blockChainHeight,BigInteger.ZERO)){
            return false;
        }
        BigInteger nearBlockHeight = blockChainBranchService.getNearBlockHeight(blockChainHeight);
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
