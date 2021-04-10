package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.NodeDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PingResponse;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
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
         * 搜索种子节点
         * 定时循环的将种子节点加入区块链系统。
         * 因为有的种子节点可能会发生故障，然后本地节点链接不上种子节点，就将种子节点丢弃。
         * 能作为种子节点的服务器，肯定会很快被修复正常的。所以定时循环的将种子节点加入区块链，保证与种子节点连接是通畅的。
         */
        new Thread(()->{
            while (true){
                try {
                    if(configurationService.isAutoSearchNode()){
                        addSeedNode();
                    }
                } catch (Exception e) {
                    logger.error("定时将种子节点加入区块链网络出现异常",e);
                }
                ThreadUtil.sleep(GlobalSetting.NodeConstant.ADD_SEED_NODE_TIME_INTERVAL);
            }
        }).start();

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
            PingResponse pingResponse = new BlockchainNodeClientImpl(new NodeDTO(node.getIp())).pingNode(null);
            if(pingResponse == null){
                nodeService.deleteNode(new NodeDTO(node.getIp()));
            }else{
                //更新节点高度
                node.setBlockchainHeight(pingResponse.getBlockchainHeight());
                nodeService.updateNode(node);
                //将远程节点知道的节点，一一进行验证这些节点的合法性，如果正常，则将这些节点加入自己的区块链网络。
                for(NodeDTO nodeDTO : pingResponse.getNodes()){
                    addAvailableNodeToDatabase(nodeDTO);
                }
            }
        }
    }

    /**
     * 若一个新的(之前没有加入过本地数据库)、可用的(网络连接是好的)的节点加入本地数据库
     */
    private void addAvailableNodeToDatabase(NodeDTO node) {
        if(configurationService.isAutoSearchNode()){
            return;
        }
        PingResponse pingResponse = new BlockchainNodeClientImpl(node).pingNode(null);
        if(pingResponse == null){
            return;
        }
        NodeEntity localNode = nodeService.queryNode(node);
        if(localNode == null){
            NodeEntity nodeBO = new NodeEntity();
            nodeBO.setIp(node.getIp());
            nodeBO.setBlockchainHeight(pingResponse.getBlockchainHeight());
            nodeService.addNode(nodeBO);
            logger.debug(String.format("自动发现节点[%s]，节点已加入节点数据库。",node.getIp()));
        }else {
            NodeEntity nodeBO = new NodeEntity();
            nodeBO.setIp(node.getIp());
            nodeBO.setBlockchainHeight(pingResponse.getBlockchainHeight());
            nodeService.updateNode(nodeBO);
        }
    }

    /**
     * 添加种子节点
     */
    private void addSeedNode() {
        for(String nodeIp: GlobalSetting.SEED_NODE_LIST){
            NodeEntity node = new NodeEntity();
            node.setIp(nodeIp);
            NodeEntity nodeBO = nodeService.queryNode(new NodeDTO(node.getIp()));
            if(nodeBO == null){
                if(configurationService.isAutoSearchNode()){
                    nodeService.addNode(node);
                }
            }
        }
    }
}
