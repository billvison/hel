package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 区块链高度搜索器
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainHeightSearcher {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainHeightSearcher.class);

    private ConfigurationService configurationService;
    private NodeService nodeService;

    public BlockchainHeightSearcher(ConfigurationService configurationService, NodeService nodeService) {

        this.configurationService = configurationService;
        this.nodeService = nodeService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    searchBlockchainHeight();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索节点的高度异常",e);
                }
                SleepUtil.sleep(GlobalSetting.NodeConstant.SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL);
            }
        }).start();
    }

    private void searchBlockchainHeight() {
        List<NodeEntity> nodes = nodeService.queryAllNodeList();
        for(NodeEntity node:nodes){
            Long blockchainHeight = new BlockchainNodeClientImpl(node.getIp()).getBlockchainHeight();
            if(blockchainHeight != null){
                node.setBlockchainHeight(blockchainHeight);
                nodeService.updateNode(node);
            }
        }
    }
}
