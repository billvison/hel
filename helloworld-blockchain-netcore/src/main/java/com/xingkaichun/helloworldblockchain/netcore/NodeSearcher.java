package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;

import java.util.List;


/**
 * 节点搜索器
 * 在区块链网络中搜寻新的节点。
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeSearcher {

    private ConfigurationService configurationService;
    private NodeService nodeService;

    public NodeSearcher(ConfigurationService configurationService, NodeService nodeService) {

        this.configurationService = configurationService;
        this.nodeService = nodeService;
    }

    public void start() {
        /*
         * 搜索区块链网络中的节点
         * 通过 向已知的网络节点发送[获取它所知道的节点列表]的请求方式 搜索区块链网络中节点
         */
        new Thread(()->{
            while (true){
                try {
                    if(configurationService.isAutoSearchNode()){
                        searchNodes();
                    }
                } catch (Exception e) {
                    LogUtil.error("在区块链网络中搜索新的节点出现异常",e);
                }
                SleepUtil.sleep(GlobalSetting.NodeConstant.SEARCH_NODE_TIME_INTERVAL);
            }
        }).start();
    }

    /**
     * 在区块链网络中搜寻新的节点
     */
    private void searchNodes() {
        //这里可以利用多线程进行性能优化，因为本项目是helloworld项目，因此只采用单线程轮询每一个节点查询新的网络节点，不做进一步优化拓展。
        List<NodeEntity> nodes = nodeService.queryAllNodeList();
        for(NodeEntity node:nodes){
            if(!configurationService.isAutoSearchNode()){
                return;
            }
            GetNodesResponse getNodesResponse = new BlockchainNodeClientImpl(node.getIp()).getNodes(new GetNodesRequest());
            if(getNodesResponse == null){
                nodeService.deleteNode(node.getIp());
                LogUtil.debug("删除节点"+node.getIp()+",原因：联不通");
                break;
            }else {
                //将远程节点知道的节点，一一进行验证这些节点的合法性，如果正常，则将这些节点加入自己的区块链网络。
                for(String nodeIp : getNodesResponse.getNodes()){
                    addAvailableNodeToDatabase(new NodeDTO(nodeIp));
                }
            }
        }
    }

    /**
     * 若一个新的(之前没有加入过本地数据库)、可用的(网络连接是好的)的节点加入本地数据库
     */
    private void addAvailableNodeToDatabase(NodeDTO node) {
        if(!configurationService.isAutoSearchNode()){
            return;
        }
        NodeEntity localNode = nodeService.queryNode(node.getIp());
        if(localNode == null){
            PingResponse pingResponse = new BlockchainNodeClientImpl(node.getIp()).pingNode(new PingRequest());
            if(pingResponse == null){
                nodeService.deleteNode(node.getIp());
                LogUtil.debug(String.format("删除节点[%s]，原因是无法联通。",node.getIp()));
                return;
            }
            NodeEntity nodeBO = new NodeEntity();
            nodeBO.setIp(node.getIp());
            nodeService.addNode(nodeBO);
            LogUtil.debug(String.format("自动发现节点[%s]，节点已加入节点数据库。",node.getIp()));
        }
    }
}
