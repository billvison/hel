package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.core.exception.BlockChainCoreException;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimerService {

    private static final Logger logger = LoggerFactory.getLogger(TimerService.class);

    @Autowired
    private BlockChainService blockChainService;

    @Autowired
    private LocalNodeService localNodeService;

    @Autowired
    private RemoteNodeService remoteNodeService;

    @Autowired
    private Gson gson;

    @Value("${node.searchNewNodeTimeInterval}")
    private long searchNewNodeTimeInterval;

    @Value("${node.searchNewBlocksTimeInterval}")
    private long searchNewBlocksTimeInterval;

    @Value("${node.checkLocalBlockChainHeightIsHighTimeInterval}")
    private long checkLocalBlockChainHeightIsHighTimeInterval;

    /**
     * 在区块链网络中搜寻新的节点
     */
    public void searchNewNodes() {
        Map<String,Node> map = new HashMap<>();
        //TODO 并发
        //向一部分节点询它所知道的节点信息
        int maxAliveNodeNumber = 1000;
        int currentAliveNodeNumber = 0;
        List<Node> nodes = localNodeService.queryNodes();
        //检测节点数据库里的节点是否可用
        for(Node node:nodes){
            //检测每一个节点是否可用
            ServiceResult<PingResponse> pingResponseServiceResult = pingNode(node);
            boolean isAvailable = pingResponseServiceResult!= null && pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS;
            node.setNodeAvailable(isAvailable);

            if(isAvailable && (++currentAliveNodeNumber>maxAliveNodeNumber)){
                break;
            }

            //重置错误链接次数
            if(isAvailable){
                node.setErrorConnectionTimes(0);
            } else {
                int errorConnectionTimes = node.getErrorConnectionTimes()+1;
                node.setErrorConnectionTimes(errorConnectionTimes);
            }
            int updateNode = localNodeService.addOrUpdateNode(node);
            //当一个节点多次链接出错，则删除节点
            if(node.getErrorConnectionTimes() > 100){
                localNodeService.deleteNode(node.getIp(),node.getPort());
            }
            if(isAvailable){
                List<Node> nodesTemp = pingResponseServiceResult.getResult().getNodeList();
                if(nodesTemp != null){
                    map.put(buildNodeKey(node),node);
                }
             }
        }
        Collection<Node> nodeSet = map.values();
        nodeSet.removeIf(node->{
            for(Node n:nodes){
                if(n.getIp().equals(node.getIp())&&n.getPort()==node.getPort()){
                    return true;
                }
            }
            return false;
        });
        //只要活着的节点
        for (Node node:nodeSet){
            updateNodeInfo(node);
        }

    }


    private String buildNodeKey(Node node){
        return node.getIp()+node.getPort();
    }

    /**
     * 发现自己的区块链高度比全网节点都要高，则广播自己的区块高度
     */
    public void broadcastNewBlock() throws Exception {
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

        //根据网络带宽设置传播宽度，这里存在可能你所发送的节点一起向你请求数据。
        if(isLocalBlockChainHighest){
            int broadcastNodeCount = 0;
            for(Node node:nodes){
                if(++broadcastNodeCount>100){
                    break;
                }
                unicastLocalBlockChainHeight(node,localBlockChainHeight);
                Thread.sleep(30*1000);
            }
        }
    }

    /**
     * 查询指定节点包含的节点列表
     */
    private List<Node> queryRemoteNodes(Node node) throws BlockChainCoreException {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeApiRoute.PING);
            String html = NetUtil.getHtml(url,new PingRequest());
            Type jsonType = new TypeToken<ServiceResult<PingResponse>>() {}.getType();
            ServiceResult<PingResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS){
                return pingResponseServiceResult.getResult().getNodeList();
            }else {
                return null;
            }
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            localNodeService.nodeErrorConnectionHandle(node.getIp(),node.getPort());
            return null;
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

    private void updateNodeInfo(Node node) {
        ServiceResult<PingResponse> pingResponseServiceResult = pingNode(node);
        boolean isAvailable = pingResponseServiceResult!= null && pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS;
        if(isAvailable){
            localNodeService.addOrUpdateNode(node);
            logger.debug(String.format("自动发现节点[%s:%d]，节点已加入节点数据库。",node.getIp(),node.getPort()));
        }
    }

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
                    broadcastNewBlock();
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
}
