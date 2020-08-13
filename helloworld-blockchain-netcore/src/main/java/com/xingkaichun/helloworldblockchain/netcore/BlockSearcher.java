package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.service.SynchronizeRemoteNodeBlockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块搜索者
 * 如果发现区块链网络中有可以进行同步的区块，则尝试去同步进本地区块链。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockSearcher {

    private static final Logger logger = LoggerFactory.getLogger(BlockSearcher.class);

    private NodeService nodeService;
    private BlockChainCoreService blockChainCoreService;
    private SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService;
    private BlockChainCore blockChainCore;
    private ConfigurationService configurationService;


    public BlockSearcher(NodeService nodeService, BlockChainCoreService blockChainCoreService
            , SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService, BlockChainCore blockChainCore
            , ConfigurationService configurationService) {

        this.nodeService = nodeService;
        this.blockChainCoreService = blockChainCoreService;
        this.synchronizeRemoteNodeBlockService = synchronizeRemoteNodeBlockService;
        this.blockChainCore = blockChainCore;
        this.configurationService = configurationService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    if(blockChainCore.getSynchronizer().isActive()){
                        searchNewBlocks();
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
    private void searchNewBlocks() {
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        BigInteger localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
        //可能存在多个节点的数据都比本地节点的区块多，但它们节点的数据可能是相同的，不应该向每个节点都去请求数据。
        for(NodeDto node:nodes){
            if(BigIntegerUtil.isLessThan(localBlockChainHeight,node.getBlockChainHeight())){
                synchronizeRemoteNodeBlockService.synchronizeRemoteNodeBlock(node);
                //同步之后，本地区块链高度已经发生改变了
                localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
            }
        }
    }
}
