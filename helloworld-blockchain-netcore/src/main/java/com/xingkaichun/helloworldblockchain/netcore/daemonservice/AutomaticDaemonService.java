package com.xingkaichun.helloworldblockchain.netcore.daemonservice;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.service.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * 定时执行：广播自身区块高度、节点寻找、区块寻找
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class AutomaticDaemonService {

    private static final Logger logger = LoggerFactory.getLogger(AutomaticDaemonService.class);

    private BlockChainCoreService blockChainCoreService;
    private NodeService nodeService;
    private SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService;
    private BlockchainNodeClientService blockchainNodeClientService;
    private BlockChainCore blockChainCore;
    private ConfigurationService configurationService;
    private Gson gson;

    public AutomaticDaemonService(BlockChainCoreService blockChainCoreService, NodeService nodeService, SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService, BlockchainNodeClientService blockchainNodeClientService, BlockChainCore blockChainCore, ConfigurationService configurationService) {
        this.blockChainCoreService = blockChainCoreService;
        this.nodeService = nodeService;
        this.synchronizeRemoteNodeBlockService = synchronizeRemoteNodeBlockService;
        this.blockchainNodeClientService = blockchainNodeClientService;
        this.blockChainCore = blockChainCore;
        this.configurationService = configurationService;

        this.gson = new Gson();
    }

    public void start() {
        //阻塞：将种子节点加入区块链
        addSeedNodeToLocalBlockchain();
        new Thread(()->{
            while (true){
                /**
                 * 定时将种子节点加入区块链，因为有的种子节点可能会发生故障，然后本地节点链接不上种子节点，就将种子节点丢弃。
                 * 能作为种子节点的服务器，肯定会很快被维护的。
                 */
                try {
                    addSeedNodeToLocalBlockchain();
                } catch (Exception e) {
                    logger.error("定时将种子节点加入区块链网络",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.ADD_SEED_NODE_TO_LOCAL_BLOCKCHAIN_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();



        new Thread(()->{
            while (true){
                try {
                    searchNewNodes();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.NODE_SEARCH_NEW_NODE_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();



        new Thread(()->{
            while (true){
                try {
                    broadcastLocalBlcokChainHeight();
                } catch (Exception e) {
                    logger.error("在区块链网络中广播自己的区块高度出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();



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

    private void addSeedNodeToLocalBlockchain() {
        for(String strNode:GlobalSetting.SEED_NODE_LIST){
            NodeDto node = new NodeDto();
            String[] nodeDetail = strNode.split(":");
            node.setIp(nodeDetail[0]);
            node.setPort(Integer.parseInt(nodeDetail[1]));
            NodeDto n = nodeService.queryNode(node);
            if(n == null){
                nodeService.addNode(node);
            }
        }
    }

    /**
     * 在区块链网络中搜寻新的节点
     */
    public void searchNewNodes() {
        //TODO 改善型功能 性能调整，并发
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.AUTO_SEARCH_NODE.name());
        if(!Boolean.valueOf(configurationDto.getConfValue())){
            return;
        }
        List<NodeDto> nodes = nodeService.queryAllNoForkNodeList();
        for(NodeDto node:nodes){
            configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.AUTO_SEARCH_NODE.name());
            if(!Boolean.valueOf(configurationDto.getConfValue())){
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
                        nodeService.addNode(node);
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
     * 发现自己的区块链高度比全网节点都要高，则广播自己的区块高度
     */
    private void broadcastLocalBlcokChainHeight() {
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        BigInteger localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
        boolean isLocalBlockChainHighest = true;
        for(NodeDto node:nodes){
            if(BigIntegerUtil.isLessThan(localBlockChainHeight,node.getBlockChainHeight())){
                isLocalBlockChainHighest = false;
                break;
            }
        }

        //TODO 改善型功能 性能调整 根据网络带宽设置传播宽度，这里存在可能你所发送的节点一起向你请求数据。
        //通知按照区块高度较高的先
        if(isLocalBlockChainHighest){
            //广播节点数量
            int broadcastNodeCount = 0;
            //排序节点
            Collections.sort(nodes,(NodeDto node1, NodeDto node2)->{
                if(BigIntegerUtil.isGreatThan(node1.getBlockChainHeight(),node2.getBlockChainHeight())){
                    return -1;
                } else if(node1.getBlockChainHeight() == node2.getBlockChainHeight()){
                    return 0;
                } else {
                    return 1;
                }
            });
            for(NodeDto node:nodes){
                blockchainNodeClientService.unicastLocalBlockChainHeight(node,localBlockChainHeight);
                ++broadcastNodeCount;
                if(broadcastNodeCount>20){
                    return;
                }
            }
        }
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
                    nodeService.addNode(node);
                    logger.debug(String.format("自动发现节点[%s:%d]，节点已加入节点数据库。",node.getIp(),node.getPort()));
                }else {
                    nodeService.updateNode(node);
                }
            }
        }
    }
}
