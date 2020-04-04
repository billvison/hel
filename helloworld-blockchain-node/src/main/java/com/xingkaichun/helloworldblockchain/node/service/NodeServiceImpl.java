package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.request.QueryNodeListRequest;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.model.NodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private ConfigurationService configurationService;

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
        NodeEntity nodeEntity = nodeDao.queryNode(node);
        if(nodeEntity == null){
            fillNodeDefaultValue(node);
            NodeEntity nodeEntityByPass = classCast(node);
            nodeDao.addNode(nodeEntityByPass);
        } else {
            NodeEntity nodeEntityByPass = classCast(node);
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
        if(errorConnectionTimes >= configurationService.getNodeErrorConnectionTimesRemoveThreshold()){
            nodeDao.deleteNode(simpleNode);
        } else {
            nodeEntity.setErrorConnectionTimes(errorConnectionTimes);
            nodeEntity.setIsNodeAvailable(false);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void addOrUpdateNodeForkPropertity(SimpleNode simpleNode) {
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNode);
        if(nodeEntity == null){
            Node node = new Node();
            node.setIp(simpleNode.getIp());
            node.setPort(simpleNode.getPort());
            node.setFork(true);
            fillNodeDefaultValue(node);
            NodeEntity nodeEntity1 = classCast(node);
            nodeDao.addNode(nodeEntity1);
        }else {
            nodeEntity.setFork(true);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void deleteNode(SimpleNode simpleNode) {
        nodeDao.deleteNode(simpleNode);
    }

    @Override
    public List<Node> queryNodeList(QueryNodeListRequest request) {
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNodeList();
        List<Node> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public void addNode(Node node) {
        fillNodeDefaultValue(node);
        NodeEntity nodeEntityByPass = classCast(node);
        nodeDao.addNode(nodeEntityByPass);
    }

    @Override
    public void updateNode(Node node) {
        NodeEntity nodeEntit = classCast(node);
        nodeDao.updateNode(nodeEntit);
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
        node.setIsNodeAvailable(nodeEntity.getIsNodeAvailable());
        node.setErrorConnectionTimes(nodeEntity.getErrorConnectionTimes());
        node.setBlockChainHeight(nodeEntity.getBlockChainHeight());
        node.setFork(nodeEntity.getFork());
        return node;
    }

    private NodeEntity classCast(Node node) {
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setIp(node.getIp());
        nodeEntity.setPort(node.getPort());
        nodeEntity.setIsNodeAvailable(node.getIsNodeAvailable());
        nodeEntity.setErrorConnectionTimes(node.getErrorConnectionTimes());
        nodeEntity.setBlockChainHeight(node.getBlockChainHeight());
        nodeEntity.setFork(node.getFork());
        return nodeEntity;
    }

    private void fillNodeDefaultValue(Node node) {
        if(node.getBlockChainHeight() == null){
            node.setBlockChainHeight(BigInteger.ZERO);
        }
        if(node.getIsNodeAvailable() == null){
            node.setIsNodeAvailable(true);
        }
        if(node.getErrorConnectionTimes() == null){
            node.setErrorConnectionTimes(0);
        }
        if(node.getFork() == null){
            node.setFork(false);
        }
    }
}
