package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockchainNodeClientService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 节点搜索器
 * 在区块链网络中搜寻新的节点。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class NodeSearcher {

    private static final Logger logger = LoggerFactory.getLogger(NodeSearcher.class);

    private ConfigurationService configurationService;
    private NodeService nodeService;
    private BlockchainNodeClientService blockchainNodeClientService;

    public NodeSearcher(ConfigurationService configurationService, NodeService nodeService
            , BlockchainNodeClientService blockchainNodeClientService) {

        this.configurationService = configurationService;
        this.nodeService = nodeService;
        this.blockchainNodeClientService = blockchainNodeClientService;
    }

    public void start() {
        /**
         * 定时循环的将种子节点加入区块链系统。
         * 因为有的种子节点可能会发生故障，然后本地节点链接不上种子节点，就将种子节点丢弃。
         * 能作为种子节点的服务器，肯定会很快被修复正常的。所以定时循环的将种子节点加入区块链，保证与种子节点连接是通畅的。
         */
        new Thread(()->{
            while (true){
                try {
                    if(configurationService.autoSearchNodeOption()){
                        addSeedNode();
                    }
                } catch (Exception e) {
                    logger.error("定时将种子节点加入区块链网络出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.ADD_SEED_NODE_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();

        //搜索新的节点
        new Thread(()->{
            while (true){
                try {
                    if(configurationService.autoSearchNodeOption()){
                        searchNewNodes();
                    }
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.SEARCH_NEW_NODE_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();
    }

    /**
     * 在区块链网络中搜寻新的节点
     */
    public void searchNewNodes() {
        //这里可以利用多线程进行性能优化，因为本项目是helloworld项目，因此只采用单线程轮询每一个节点查询新的网络节点，不做进一步优化拓展。
        List<NodeDto> nodes = nodeService.queryAllNoForkNodeList();
        for(NodeDto node:nodes){
            if(!configurationService.autoSearchNodeOption()){
                return;
            }
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
            boolean isPingSuccess = ServiceResult.isSuccess(pingResponseServiceResult);
            node.setIsNodeAvailable(isPingSuccess);
            if(isPingSuccess){
                PingResponse pingResponse = pingResponseServiceResult.getResult();
                //链ID不同
                if(!GlobalSetting.BLOCK_CHAIN_ID.equals(pingResponse.getBlockChainId())){
                    nodeService.deleteNode(node);
                }else {
                    node.setBlockChainHeight(pingResponse.getBlockChainHeight());
                    node.setErrorConnectionTimes(0);
                    if(nodeService.queryNode(node) == null){
                        if(configurationService.autoSearchNodeOption()){
                            nodeService.addNode(node);
                        }
                    }else {
                        nodeService.updateNode(node);
                    }
                    //处理节点传输过来它所知道的节点列表
                    addNewAvailableNodeToDatabase(pingResponse.getNodeList());
                }
            } else {
                nodeService.nodeErrorConnectionHandle(node);
            }
        }
    }

    /**
     * 将远程节点知道的节点，一一进行验证这些节点的合法性，如果正常，则将这些节点加入自己的区块链网络。
     */
    private void addNewAvailableNodeToDatabase(List<NodeDto> nodeList){
        if(nodeList == null || nodeList.size()==0){
            return;
        }
        for(NodeDto node : nodeList){
            addNewAvailableNodeToDatabase(node);
        }
    }

    /**
     * 若一个新的(之前没有加入过本地数据库)、可用的(网络连接是好的)的节点加入本地数据库
     */
    private void addNewAvailableNodeToDatabase(NodeDto node) {
        NodeDto localNode = nodeService.queryNode(node);
        if(localNode == null){
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                node.setIsNodeAvailable(true);
                node.setBlockChainHeight(pingResponseServiceResult.getResult().getBlockChainHeight());
                node.setErrorConnectionTimes(0);
                if(nodeService.queryNode(node) == null){
                    if(configurationService.autoSearchNodeOption()){
                        nodeService.addNode(node);
                    }
                    logger.debug(String.format("自动发现节点[%s:%d]，节点已加入节点数据库。",node.getIp(),node.getPort()));
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
        for(String strNode: GlobalSetting.SEED_NODE_LIST){
            NodeDto node = new NodeDto();
            String[] nodeDetail = strNode.split(":");
            node.setIp(nodeDetail[0]);
            node.setPort(Integer.parseInt(nodeDetail[1]));
            NodeDto nodeDto = nodeService.queryNode(node);
            if(nodeDto == null){
                if(configurationService.autoSearchNodeOption()){
                    nodeService.addNode(node);
                }
            }
        }
    }
}
