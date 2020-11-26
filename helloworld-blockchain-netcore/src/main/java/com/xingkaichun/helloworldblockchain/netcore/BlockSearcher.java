package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.service.SynchronizeRemoteNodeBlockService;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
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

    private NodeService nodeService;
    private SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService;
    private BlockchainCore blockchainCore;
    private BlockchainNodeClient blockchainNodeClient;


    public BlockSearcher(NodeService nodeService
            , SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService, BlockchainCore blockchainCore
            , BlockchainNodeClient blockchainNodeClient) {
        this.nodeService = nodeService;
        this.synchronizeRemoteNodeBlockService = synchronizeRemoteNodeBlockService;
        this.blockchainCore = blockchainCore;
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
                    if(blockchainCore.getSynchronizer().isActive()){
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
                synchronizeRemoteNodeBlockService.synchronizeRemoteNodeBlock(node);
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
}
