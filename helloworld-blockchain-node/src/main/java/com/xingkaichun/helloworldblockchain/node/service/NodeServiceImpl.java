package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

    @Value("${nodeserver.errorConnectionTimesRemoveThreshold}")
    private int errorConnectionTimesRemoveThreshold;

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private BlockChainCore blockChainCore;


    @Override
    public List<Node> queryNodes() {
        return nodeDao.queryAllNodeList();
    }

    @Override
    public List<Node> queryAliveNodes() {
        return nodeDao.queryAliveNodes();
    }

    @Override
    public boolean addOrUpdateNode(Node node) {
        if("".equals(node.getIp()) || node.getPort()==0){
            return false;
        }
        Node dbNode = nodeDao.queryNode(node.getIp(),node.getPort());
        if(dbNode == null){
            nodeDao.addNode(node);
        } else {
            nodeDao.updateNode(node);
        }
        return true;
    }

    @Override
    public void deleteNode(SimpleNode simpleNode) {
        nodeDao.deleteNode(simpleNode.getIp(),simpleNode.getPort());
    }

    @Override
    public void nodeErrorConnectionHandle(SimpleNode simpleNode) {
        Node node = nodeDao.queryNode(simpleNode.getIp(),simpleNode.getPort());
        if(node == null){
            return;
        }
        int errorConnectionTimes = node.getErrorConnectionTimes()+1;
        if(errorConnectionTimes >= errorConnectionTimesRemoveThreshold){
            nodeDao.deleteNode(simpleNode.getIp(),simpleNode.getPort());
        }else {
            node.setErrorConnectionTimes(errorConnectionTimes);
            node.setNodeAvailable(false);
            nodeDao.updateNode(node);
        }
    }

    @Override
    public Node queryNode(SimpleNode simpleNode) {
        Node node = nodeDao.queryNode(simpleNode.getIp(),simpleNode.getPort());
        return node;
    }

}
