package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.node.service.BlockchainNodeClientService;
import com.xingkaichun.helloworldblockchain.node.service.NodeService;
import com.xingkaichun.helloworldblockchain.node.service.SynchronizeRemoteNodeBlockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * 定时执行：广播自身区块高度、节点寻找、区块寻找
 */
public class TimerService {

    private static final Logger logger = LoggerFactory.getLogger(TimerService.class);

    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService;

    @Autowired
    private BlockchainNodeClientService blockchainNodeClientService;

    @Value("${nodeserver.searchNewNodeTimeInterval}")
    private long searchNewNodeTimeInterval;

    @Value("${nodeserver.searchNewBlocksTimeInterval}")
    private long searchNewBlocksTimeInterval;

    @Value("${nodeserver.checkLocalBlockChainHeightIsHighTimeInterval}")
    private long checkLocalBlockChainHeightIsHighTimeInterval;

    @Autowired
    private Gson gson;


    @PostConstruct
    private void startThread(){

        new Thread(()->{
            while (true){
                try {
                    searchNewNodes();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                try {
                    Thread.sleep(searchNewNodeTimeInterval);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        new Thread(()->{
            while (true){
                try {
                    broadcastLocalBlcokChainHeight();
                } catch (Exception e) {
                    logger.error("在区块链网络中广播自己的区块高度出现异常",e);
                }
                try {
                    Thread.sleep(checkLocalBlockChainHeightIsHighTimeInterval);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        new Thread(()->{
            while (true){
                try {
                    searchNewBlocks();
                } catch (Exception e) {
                    logger.error("在区块链网络中同步其它节点的区块出现异常",e);
                }
                try {
                    Thread.sleep(searchNewBlocksTimeInterval);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    /**
     * 在区块链网络中搜寻新的节点
     */
    public void searchNewNodes() {
        //TODO 性能调整，并发
        List<Node> nodes = nodeService.queryNodes();
        for(Node node:nodes){
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
            boolean isPingSuccess = ServiceResult.isSuccess(pingResponseServiceResult);
            node.setNodeAvailable(isPingSuccess);
            if(isPingSuccess){
                PingResponse pingResponse = pingResponseServiceResult.getResult();
                node.setBlockChainHeight(pingResponse.getBlockChainHeight());
                node.setErrorConnectionTimes(0);
                //更新节点
                nodeService.addOrUpdateNode(node);
                //处理节点传输过来它所知道的节点列表
                addNewAvailableNodeToDatabase(pingResponse.getNodeList());
            } else {
                nodeService.nodeErrorConnectionHandle(node.getIp(),node.getPort());
            }
        }
    }

    /**
     * 发现自己的区块链高度比全网节点都要高，则广播自己的区块高度
     */
    private void broadcastLocalBlcokChainHeight() throws Exception {
        List<Node> nodes = nodeService.queryAliveNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        int localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
        boolean isLocalBlockChainHighest = true;
        for(Node node:nodes){
            if(localBlockChainHeight < node.getBlockChainHeight()){
                isLocalBlockChainHighest = false;
                break;
            }
        }

        //TODO 性能调整 根据网络带宽设置传播宽度，这里存在可能你所发送的节点一起向你请求数据。
        //通知按照区块高度较高的先
        if(isLocalBlockChainHighest){
            //广播节点数量
            int broadcastNodeCount = 0;
            //排序节点
            Collections.sort(nodes,(Node node1,Node node2)->{
                if(node1.getBlockChainHeight() > node2.getBlockChainHeight()){
                    return -1;
                } else if(node1.getBlockChainHeight() == node2.getBlockChainHeight()){
                    return 0;
                } else {
                    return 1;
                }
            });
            for(Node node:nodes){
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
    private void searchNewBlocks() throws Exception {
        List<Node> nodes = nodeService.queryAliveNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        int localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
        //可能存在多个节点的数据都比本地节点的区块多，但它们节点的数据可能是相同的，不应该向每个节点都去请求数据。
        for(Node node:nodes){
            if(localBlockChainHeight < node.getBlockChainHeight()){
                synchronizeRemoteNodeBlockService.synchronizeRemoteNodeBlock(node);
                //同步之后，本地区块链高度已经发生改变了
                localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
            }
        }
    }

    /**
     * 将远程节点知道的节点，一一进行验证这些节点的合法性，如果正常，则将这些节点加入自己的区块链网络。
     */
    private void addNewAvailableNodeToDatabase(List<Node> nodeList) {
        if(nodeList == null || nodeList.size()==0){
            return;
        }
        for(Node node : nodeList){
            addNewAvailableNodeToDatabase(node);
        }
    }

    /**
     * 若一个新的(之前没有加入过本地数据库)、可用的(网络连接是好的)的节点加入本地数据库
     */
    private void addNewAvailableNodeToDatabase(Node node) {
        Node localNode = nodeService.queryNode(node.getIp(),node.getPort());
        if(localNode == null){
            ServiceResult<PingResponse> pingResponseServiceResult = blockchainNodeClientService.pingNode(node);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                node.setNodeAvailable(true);
                node.setBlockChainHeight(pingResponseServiceResult.getResult().getBlockChainHeight());
                node.setErrorConnectionTimes(0);
                nodeService.addOrUpdateNode(node);
                logger.debug(String.format("自动发现节点[%s:%d]，节点已加入节点数据库。",node.getIp(),node.getPort()));
            }
        }
    }
}
