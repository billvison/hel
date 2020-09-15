package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.utils.LongUtil;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.service.*;
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
    private BlockChainCoreService blockChainCoreService;
    private SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService;
    private BlockChainCore blockChainCore;
    private ConfigurationService configurationService;
    private BlockchainNodeClientService blockchainNodeClientService;


    public BlockSearcher(NodeService nodeService, BlockChainCoreService blockChainCoreService
            , SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService, BlockChainCore blockChainCore
            , ConfigurationService configurationService,BlockchainNodeClientService blockchainNodeClientService) {

        this.nodeService = nodeService;
        this.blockChainCoreService = blockChainCoreService;
        this.synchronizeRemoteNodeBlockService = synchronizeRemoteNodeBlockService;
        this.blockChainCore = blockChainCore;
        this.configurationService = configurationService;
        this.blockchainNodeClientService = blockchainNodeClientService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    if(blockSearchOption()){
                        searchNewBlocks();
                    }
                } catch (Exception e) {
                    logger.error("在区块链网络中同步其它节点的区块出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.BLOCK_SEARCH_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();
        new Thread(()->{
            while (true){
                try {
                    if(blockChainCore.getSynchronizer().isActive()){
                        downloadNewBlocks();
                    }
                } catch (Exception e) {
                    logger.error("在区块链网络中同步其它节点的区块出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.SEARCH_NEW_BLOCKS_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();
    }

    /**
     * 搜索新的区块，并同步这些区块到本地区块链系统
     */
    private void downloadNewBlocks() {
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        long localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
        //可能存在多个节点的数据都比本地节点的区块多，但它们节点的数据可能是相同的，不应该向每个节点都去请求数据。
        for(NodeDto node:nodes){
            if(LongUtil.isLessThan(localBlockChainHeight,node.getBlockChainHeight())){
                synchronizeRemoteNodeBlockService.synchronizeRemoteNodeBlock(node);
                //同步之后，本地区块链高度已经发生改变了
                localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
            }
        }
    }


    /**
     * 在区块链网络中搜寻新的节点
     */
    public void searchNewBlocks() {
        List<NodeDto> nodes = nodeService.queryAllNoForkNodeList();
        for(NodeDto node:nodes){
            if(!blockSearchOption()){
                return;
            }
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
            boolean isPingSuccess = ServiceResult.isSuccess(pingResponseServiceResult);
            node.setIsNodeAvailable(isPingSuccess);
            if(isPingSuccess){
                PingResponse pingResponse = pingResponseServiceResult.getResult();
                node.setBlockChainHeight(pingResponse.getBlockChainHeight());
                node.setErrorConnectionTimes(0);
                nodeService.updateNode(node);
            } else {
                nodeService.nodeErrorConnectionHandle(node);
            }
        }
    }

    /**
     * 是否广播自己
     */
    private boolean blockSearchOption() {
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.BLOCK_SEARCH.name());
        return Boolean.valueOf(configurationDto.getConfValue());
    }
}
