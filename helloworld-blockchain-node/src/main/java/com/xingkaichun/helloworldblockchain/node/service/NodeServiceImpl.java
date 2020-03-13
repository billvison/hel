package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
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
    public void deleteNode(String ip, int port) {
        nodeDao.deleteNode(ip,port);
    }

    @Override
    public void nodeErrorConnectionHandle(String ip, int port) {
        Node node = nodeDao.queryNode(ip,port);
        if(node == null){
            return;
        }
        int errorConnectionTimes = node.getErrorConnectionTimes()+1;
        if(errorConnectionTimes >= errorConnectionTimesRemoveThreshold){
            nodeDao.deleteNode(ip,port);
        }else {
            node.setErrorConnectionTimes(errorConnectionTimes);
            node.setNodeAvailable(false);
            nodeDao.updateNode(node);
        }
    }

    @Override
    public Node queryNode(String ip, int port) {
        Node node = nodeDao.queryNode(ip,port);
        return node;
    }

}
