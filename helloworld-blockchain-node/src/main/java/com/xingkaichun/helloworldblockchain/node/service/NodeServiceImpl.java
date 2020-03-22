package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.model.NodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

    @Value("${node.errorConnectionTimesRemoveThreshold}")
    private int errorConnectionTimesRemoveThreshold;

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private BlockChainCore blockChainCore;

    @Override
    public List<Node> queryAllNoForkNodeList() {
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNoForkNodeList();
        List<Node> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public List<Node> queryAllNoForkAliveNodeList() {
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNoForkAliveNodeList();
        List<Node> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public boolean addOrUpdateNode(Node node) {
        if("".equals(node.getIp()) || node.getPort()==0){
            return false;
        }
        NodeEntity nodeEntity = nodeDao.queryNode(node);
        NodeEntity nodeEntityByPass = classCast(node);
        if(nodeEntity == null){
            nodeDao.addNode(nodeEntityByPass);
        } else {
            nodeDao.updateNode(nodeEntityByPass);
        }
        return true;
    }

    @Override
    public void nodeErrorConnectionHandle(SimpleNode simpleNode) {
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNode);
        if(nodeEntity == null){
            return;
        }
        int errorConnectionTimes = nodeEntity.getErrorConnectionTimes()+1;
        if(errorConnectionTimes >= errorConnectionTimesRemoveThreshold){
            nodeDao.deleteNode(simpleNode);
        } else {
            nodeEntity.setErrorConnectionTimes(errorConnectionTimes);
            nodeEntity.setNodeAvailable(false);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void setNodeFork(SimpleNode simpleNode) {
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNode);
        if(nodeEntity == null){
            NodeEntity nodeEntity1 = new NodeEntity();
            nodeEntity1.setIp(simpleNode.getIp());
            nodeEntity1.setPort(simpleNode.getPort());
            nodeEntity1.setFork(true);
            nodeDao.addNode(nodeEntity1);
        }else {
            nodeEntity.setFork(true);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public Node queryNode(SimpleNode simpleNode) {
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNode);
        if(nodeEntity == null){
            return null;
        }
        return classCast(nodeEntity);
    }


    private List<Node> classCast(List<NodeEntity> nodeEntityList) {
        if(nodeEntityList == null){
            return null;
        }
        List<Node> nodeList = new ArrayList<>();
        for(NodeEntity nodeEntity:nodeEntityList){
            nodeList.add(classCast(nodeEntity));
        }
        return nodeList;
    }

    private Node classCast(NodeEntity nodeEntity) {
        Node node = new Node();
        node.setIp(nodeEntity.getIp());
        node.setPort(nodeEntity.getPort());
        node.setNodeAvailable(nodeEntity.isNodeAvailable());
        node.setErrorConnectionTimes(nodeEntity.getErrorConnectionTimes());
        node.setBlockChainHeight(nodeEntity.getBlockChainHeight());
        node.setFork(nodeEntity.isFork());
        return node;
    }

    private NodeEntity classCast(Node node) {
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setIp(node.getIp());
        nodeEntity.setPort(node.getPort());
        nodeEntity.setNodeAvailable(node.isNodeAvailable());
        nodeEntity.setErrorConnectionTimes(node.getErrorConnectionTimes());
        nodeEntity.setBlockChainHeight(node.getBlockChainHeight());
        nodeEntity.setFork(node.isFork());
        return nodeEntity;
    }
}
