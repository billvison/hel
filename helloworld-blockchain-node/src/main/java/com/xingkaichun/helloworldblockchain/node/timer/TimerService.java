package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceCode;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.node.Node;
import com.xingkaichun.helloworldblockchain.node.dto.node.NodeApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.AddOrUpdateNodeRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.PingRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.response.AddOrUpdateNodeResponse;
import com.xingkaichun.helloworldblockchain.node.dto.node.response.PingResponse;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainService;
import com.xingkaichun.helloworldblockchain.node.service.LocalNodeService;
import com.xingkaichun.helloworldblockchain.node.service.RemoteNodeService;
import com.xingkaichun.helloworldblockchain.node.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 定时执行：广播自身区块高度、节点寻找、区块寻找
 */
public class TimerService {

    private static final Logger logger = LoggerFactory.getLogger(TimerService.class);

    @Autowired
    private BlockChainService blockChainService;

    @Autowired
    private LocalNodeService localNodeService;

    @Autowired
    private RemoteNodeService remoteNodeService;

    @Value("${node.searchNewNodeTimeInterval}")
    private long searchNewNodeTimeInterval;

    @Value("${node.searchNewBlocksTimeInterval}")
    private long searchNewBlocksTimeInterval;

    @Value("${node.checkLocalBlockChainHeightIsHighTimeInterval}")
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
                    System.exit(1);
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
                    System.exit(1);
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
                    System.exit(1);
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
        List<Node> nodes = localNodeService.queryNodes();
        for(Node node:nodes){
            ServiceResult<PingResponse> pingResponseServiceResult = pingNode(node);
            boolean isAvailable = pingResponseServiceResult!= null && pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS;
            node.setNodeAvailable(isAvailable);
            if(isAvailable){
                PingResponse pingResponse = pingResponseServiceResult.getResult();
                node.setBlockChainHeight(pingResponse.getBlockChainHeight());
                node.setErrorConnectionTimes(0);
                localNodeService.addOrUpdateNode(node);

                List<Node> nodesTemp = pingResponse.getNodeList();
                for(Node nodeTemp : nodesTemp){
                    addNewAvailableNodeToDatabase(nodeTemp);
                }
            } else {
                localNodeService.nodeErrorConnectionHandle(node.getIp(),node.getPort());
            }
        }
    }

    /**
     * 发现自己的区块链高度比全网节点都要高，则广播自己的区块高度
     */
    private void broadcastLocalBlcokChainHeight() throws Exception {
        List<Node> nodes = localNodeService.queryAliveNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        int localBlockChainHeight = blockChainService.queryBlockChainHeight();
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
            int broadcastNodeCount = 0;
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
                if(localBlockChainHeight-node.getBlockChainHeight()<5){
                    unicastLocalBlockChainHeight(node,localBlockChainHeight);
                    ++broadcastNodeCount;
                }
                if(broadcastNodeCount>20){
                    return;
                }
            }
        }
    }

    /**
     * 搜索新的区块
     */
    private void searchNewBlocks() throws Exception {
        List<Node> nodes = localNodeService.queryAliveNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        int localBlockChainHeight = blockChainService.queryBlockChainHeight();
        //可能存在多个节点的数据都比本地节点的区块多，但它们节点的数据可能是相同的，不应该向每个节点都去请求数据。
        for(Node node:nodes){
            if(localBlockChainHeight < node.getBlockChainHeight()){
                remoteNodeService.synchronizeRemoteNodeBlock(node);
            }
            localBlockChainHeight = blockChainService.queryBlockChainHeight();
        }
    }

    /**
     * Ping指定节点
     */
    private ServiceResult<PingResponse> pingNode(Node node) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeApiRoute.PING);
            String html = NetUtil.getHtml(url,new PingRequest());
            Type jsonType = new TypeToken<ServiceResult<PingResponse>>() {}.getType();
            ServiceResult<PingResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            return pingResponseServiceResult;
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            localNodeService.nodeErrorConnectionHandle(node.getIp(),node.getPort());
            return null;
        }
    }

    /**
     * 单播：将本地区块链高度传给指定节点
     */
    private boolean unicastLocalBlockChainHeight(Node node,int localBlockChainHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeApiRoute.ADD_OR_UPDATE_NODE);
            AddOrUpdateNodeRequest request = new AddOrUpdateNodeRequest();
            request.setBlockChainHeight(localBlockChainHeight);
            String html = NetUtil.getHtml(url,request);
            Type jsonType = new TypeToken<ServiceResult<AddOrUpdateNodeResponse>>() {}.getType();
            ServiceResult<AddOrUpdateNodeResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            return pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS;
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            localNodeService.nodeErrorConnectionHandle(node.getIp(),node.getPort());
            return false;
        }
    }

    /**
     * 若一个新的(之前没有加入过本地数据库)、可用的(网络连接是好的)的节点加入本地数据库
     * @param node
     */
    private void addNewAvailableNodeToDatabase(Node node) {
        Node localNode = localNodeService.queryNode(node.getIp(),node.getPort());
        if(localNode == null){
            ServiceResult<PingResponse> pingResponseServiceResult = pingNode(node);
            boolean isAvailable = pingResponseServiceResult!= null && pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS;
            if(isAvailable){
                node.setNodeAvailable(true);
                node.setBlockChainHeight(pingResponseServiceResult.getResult().getBlockChainHeight());
                node.setErrorConnectionTimes(0);
                localNodeService.addOrUpdateNode(node);
                logger.debug(String.format("自动发现节点[%s:%d]，节点已加入节点数据库。",node.getIp(),node.getPort()));
            }
        }
    }
}
