package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.API;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.NodeDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 节点搜索器
 * 在区块链网络中搜寻新的节点。
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeSearcher {

    private static final Logger logger = LoggerFactory.getLogger(NodeSearcher.class);

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
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                ThreadUtil.sleep(GlobalSetting.NodeConstant.SEARCH_NEW_NODE_TIME_INTERVAL);
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
            String[] nodesResp = new BlockchainNodeClientImpl(node.getIp()).getNodes();
            if(nodesResp == null){
                break;
            }else {
                //将远程节点知道的节点，一一进行验证这些节点的合法性，如果正常，则将这些节点加入自己的区块链网络。
                for(String nodeIp : nodesResp){
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
            String pingResponse = new BlockchainNodeClientImpl(node.getIp()).pingNode();
            if(!StringUtil.isEquals(API.Response.OK,pingResponse)){
                nodeService.deleteNode(node.getIp());
                logger.debug(String.format("删除节点[%s]，原因是无法联通。",node.getIp()));
                return;
            }
            NodeEntity nodeBO = new NodeEntity();
            nodeBO.setIp(node.getIp());
            nodeService.addNode(nodeBO);
            logger.debug(String.format("自动发现节点[%s]，节点已加入节点数据库。",node.getIp()));
        }
    }
}
