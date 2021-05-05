package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetcoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.GetBlockchianHeightRequest;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.GetBlockchianHeightResponse;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;

import java.util.List;


/**
 * 区块链高度搜索器
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainHeightSearcher {

    private NetcoreConfiguration netcoreConfiguration;
    private NodeService nodeService;

    public BlockchainHeightSearcher(NetcoreConfiguration netcoreConfiguration, NodeService nodeService) {

        this.netcoreConfiguration = netcoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    searchBlockchainHeight();
                    SleepUtil.sleep(netcoreConfiguration.getSearchBlockchainHeightTimeInterval());
                } catch (Exception e) {
                    SystemUtil.errorExit("在区块链网络中搜索节点的高度异常",e);
                }
            }
        }).start();
    }

    private void searchBlockchainHeight() {
        List<Node> nodes = nodeService.queryAllNodeList();
        for(Node node:nodes){
            try {
                GetBlockchianHeightRequest getBlockchianHeightRequest = new GetBlockchianHeightRequest();
                GetBlockchianHeightResponse getBlockchianHeightResponse = new BlockchainNodeClientImpl(node.getIp()).getBlockchainHeight(getBlockchianHeightRequest);
                if(getBlockchianHeightResponse != null){
                    node.setBlockchainHeight(getBlockchianHeightResponse.getBlockchainHeight());
                    nodeService.updateNode(node);
                }
            }catch (Exception e){
                LogUtil.error(StringUtil.format("搜索节点[%s]的高度异常。",node.getIp()),e);
            }
        }
    }
}
