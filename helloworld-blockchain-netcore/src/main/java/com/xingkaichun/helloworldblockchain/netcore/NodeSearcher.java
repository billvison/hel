package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDTO;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
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
    private BlockchainNodeClient blockchainNodeClient;

    public NodeSearcher(ConfigurationService configurationService, NodeService nodeService
            , BlockchainNodeClient blockchainNodeClient) {

        this.configurationService = configurationService;
        this.nodeService = nodeService;
        this.blockchainNodeClient = blockchainNodeClient;
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
        List<NodeDTO> nodes = nodeService.queryAllNoForkNodeList();
        for(NodeDTO node:nodes){
            if(!configurationService.isAutoSearchNode()){
                return;
            }
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClient.pingNode(node);
            boolean isPingSuccess = ServiceResult.isSuccess(pingResponseServiceResult);
            node.setIsNodeAvailable(isPingSuccess);
            if(isPingSuccess){
                PingResponse pingResponse = pingResponseServiceResult.getResult();
                node.setBlockchainHeight(pingResponse.getBlockchainHeight());
                node.setErrorConnectionTimes(0);
                if(nodeService.queryNode(node) == null){
                    if(configurationService.isAutoSearchNode()){
                        nodeService.addNode(node);
                    }
                }else {
                    nodeService.updateNode(node);
                }
                //处理节点传输过来它所知道的节点列表
                addAvailableNodeToDatabase(pingResponse.getNodeList());
            } else {
                nodeService.nodeConnectionErrorHandle(node);
            }
        }
    }

    /**
     * 将远程节点知道的节点，一一进行验证这些节点的合法性，如果正常，则将这些节点加入自己的区块链网络。
     */
    private void addAvailableNodeToDatabase(List<NodeDTO> nodeList){
        if(nodeList == null){
            return;
        }
        for(NodeDTO node : nodeList){
            addAvailableNodeToDatabase(node);
        }
    }

    /**
     * 若一个新的(之前没有加入过本地数据库)、可用的(网络连接是好的)的节点加入本地数据库
     */
    private void addAvailableNodeToDatabase(NodeDTO node) {
        if(configurationService.isAutoSearchNode()){
            return;
        }
        NodeDTO localNode = nodeService.queryNode(node);
        if(localNode == null){
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClient.pingNode(node);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                node.setIsNodeAvailable(true);
                node.setBlockchainHeight(pingResponseServiceResult.getResult().getBlockchainHeight());
                node.setErrorConnectionTimes(0);
                if(nodeService.queryNode(node) == null){
                    nodeService.addNode(node);
                    logger.debug(String.format("自动发现节点[%s]，节点已加入节点数据库。",node.getIp()));
                }else {
                    nodeService.updateNode(node);
                }
            }
        }
    }

    /**
     * 添加种子节点
     */
    private void addSeedNode() {
        for(String nodeIp: GlobalSetting.SEED_NODE_LIST){
            NodeDTO node = new NodeDTO();
            node.setIp(nodeIp);
            NodeDTO nodeDto = nodeService.queryNode(node);
            if(nodeDto == null){
                if(configurationService.isAutoSearchNode()){
                    nodeService.addNode(node);
                }
            }
        }
    }
}
