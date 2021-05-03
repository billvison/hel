package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.GetBlockchianHeightRequest;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.GetBlockchianHeightResponse;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;

import java.util.List;


/**
 * 区块链高度搜索器
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainHeightSearcher {

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
                    LogUtil.error("在区块链网络中搜索节点的高度异常",e);
                }
                SleepUtil.sleep(GlobalSetting.NodeConstant.SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL);
            }
        }).start();
    }

    private void searchBlockchainHeight() {
        List<NodeEntity> nodes = nodeService.queryAllNodeList();
        for(NodeEntity node:nodes){
            GetBlockchianHeightRequest getBlockchianHeightRequest = new GetBlockchianHeightRequest();
            GetBlockchianHeightResponse getBlockchianHeightResponse = new BlockchainNodeClientImpl(node.getIp()).getBlockchainHeight(getBlockchianHeightRequest);
            if(getBlockchianHeightResponse != null){
                node.setBlockchainHeight(getBlockchianHeightResponse.getBlockchainHeight());
                nodeService.updateNode(node);
            }
        }
    }
}
