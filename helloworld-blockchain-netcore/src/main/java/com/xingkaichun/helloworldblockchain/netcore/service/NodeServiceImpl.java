package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDTO;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDTO;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeServiceImpl implements NodeService {

    private NodeDao nodeDao;

    public NodeServiceImpl(NodeDao nodeDao) {
        this.nodeDao = nodeDao;
    }

    @Override
    public List<NodeDTO> queryAllNoForkNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNoForkNodeList();
        List<NodeDTO> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public List<NodeDTO> queryAllNoForkAliveNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNoForkAliveNodeList();
        List<NodeDTO> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public void nodeConnectionErrorHandle(BaseNodeDTO baseNodeDTO){
        NodeEntity nodeEntity = nodeDao.queryNode(baseNodeDTO.getIp());
        if(nodeEntity == null){
            return;
        }
        int errorConnectionTimes = nodeEntity.getErrorConnectionTimes()+1;
        if(errorConnectionTimes >= GlobalSetting.NodeConstant.NODE_ERROR_CONNECTION_TIMES_DELETE_THRESHOLD){
            nodeDao.deleteNode(baseNodeDTO.getIp());
        } else {
            nodeEntity.setErrorConnectionTimes(errorConnectionTimes);
            nodeEntity.setIsNodeAvailable(false);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void setNodeFork(BaseNodeDTO baseNodeDTO){
        NodeEntity nodeEntity = nodeDao.queryNode(baseNodeDTO.getIp());
        if(nodeEntity == null){
            nodeEntity = new NodeEntity();
            nodeEntity.setIp(baseNodeDTO.getIp());
            nodeEntity.setFork(true);
            fillNodeDefaultValue(nodeEntity);
            nodeDao.addNode(nodeEntity);
        }else {
            nodeEntity.setFork(true);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void deleteNode(BaseNodeDTO baseNodeDTO){
        nodeDao.deleteNode(baseNodeDTO.getIp());
    }

    @Override
    public List<NodeDTO> queryAllNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNodeList();
        List<NodeDTO> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public void addNode(NodeDTO node){
        NodeEntity nodeEntity = classCast(node);
        fillNodeDefaultValue(nodeEntity);
        nodeDao.addNode(nodeEntity);
    }

    @Override
    public void updateNode(NodeDTO node){
        NodeEntity nodeEntit = classCast(node);
        nodeDao.updateNode(nodeEntit);
    }

    @Override
    public NodeDTO queryNode(BaseNodeDTO baseNodeDTO){
        NodeEntity nodeEntity = nodeDao.queryNode(baseNodeDTO.getIp());
        if(nodeEntity == null){
            return null;
        }
        return classCast(nodeEntity);
    }


    private List<NodeDTO> classCast(List<NodeEntity> nodeEntityList) {
        if(nodeEntityList == null){
            return null;
        }
        List<NodeDTO> nodeList = new ArrayList<>();
        for(NodeEntity nodeEntity:nodeEntityList){
            nodeList.add(classCast(nodeEntity));
        }
        return nodeList;
    }

    private NodeDTO classCast(NodeEntity nodeEntity) {
        NodeDTO node = new NodeDTO();
        node.setIp(nodeEntity.getIp());
        node.setIsNodeAvailable(nodeEntity.getIsNodeAvailable());
        node.setErrorConnectionTimes(nodeEntity.getErrorConnectionTimes());
        node.setBlockchainHeight(nodeEntity.getBlockchainHeight());
        node.setFork(nodeEntity.getFork());
        return node;
    }

    private NodeEntity classCast(NodeDTO node) {
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setIp(node.getIp());
        nodeEntity.setIsNodeAvailable(node.getIsNodeAvailable());
        nodeEntity.setErrorConnectionTimes(node.getErrorConnectionTimes());
        nodeEntity.setBlockchainHeight(node.getBlockchainHeight());
        nodeEntity.setFork(node.getFork());
        return nodeEntity;
    }

    private void fillNodeDefaultValue(NodeEntity nodeEntity) {
        if(nodeEntity.getBlockchainHeight() == null){
            nodeEntity.setBlockchainHeight(GlobalSetting.GenesisBlock.HEIGHT);
        }
        if(nodeEntity.getIsNodeAvailable() == null){
            nodeEntity.setIsNodeAvailable(true);
        }
        if(nodeEntity.getErrorConnectionTimes() == null){
            nodeEntity.setErrorConnectionTimes(0);
        }
        if(nodeEntity.getFork() == null){
            nodeEntity.setFork(false);
        }
    }
}
