package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春
 */
public class NodeServiceImpl implements NodeService {

    private NodeDao nodeDao;

    public NodeServiceImpl(NodeDao nodeDao) {
        this.nodeDao = nodeDao;
    }

    @Override
    public List<NodeDto> queryAllNoForkNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNoForkNodeList();
        List<NodeDto> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public List<NodeDto> queryAllNoForkAliveNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNoForkAliveNodeList();
        List<NodeDto> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public void nodeConnectionErrorHandle(BaseNodeDto baseNodeDto){
        NodeEntity nodeEntity = nodeDao.queryNode(baseNodeDto.getIp());
        if(nodeEntity == null){
            return;
        }
        int errorConnectionTimes = nodeEntity.getErrorConnectionTimes()+1;
        if(errorConnectionTimes >= GlobalSetting.NodeConstant.NODE_ERROR_CONNECTION_TIMES_DELETE_THRESHOLD){
            nodeDao.deleteNode(baseNodeDto.getIp());
        } else {
            nodeEntity.setErrorConnectionTimes(errorConnectionTimes);
            nodeEntity.setIsNodeAvailable(false);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void setNodeFork(BaseNodeDto baseNodeDto){
        NodeEntity nodeEntity = nodeDao.queryNode(baseNodeDto.getIp());
        if(nodeEntity == null){
            nodeEntity = new NodeEntity();
            nodeEntity.setIp(baseNodeDto.getIp());
            nodeEntity.setFork(true);
            fillNodeDefaultValue(nodeEntity);
            nodeDao.addNode(nodeEntity);
        }else {
            nodeEntity.setFork(true);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void deleteNode(BaseNodeDto baseNodeDto){
        nodeDao.deleteNode(baseNodeDto.getIp());
    }

    @Override
    public List<NodeDto> queryAllNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNodeList();
        List<NodeDto> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public void addNode(NodeDto node){
        NodeEntity nodeEntity = classCast(node);
        fillNodeDefaultValue(nodeEntity);
        nodeDao.addNode(nodeEntity);
    }

    @Override
    public void updateNode(NodeDto node){
        NodeEntity nodeEntit = classCast(node);
        nodeDao.updateNode(nodeEntit);
    }

    @Override
    public NodeDto queryNode(BaseNodeDto baseNodeDto){
        NodeEntity nodeEntity = nodeDao.queryNode(baseNodeDto.getIp());
        if(nodeEntity == null){
            return null;
        }
        return classCast(nodeEntity);
    }


    private List<NodeDto> classCast(List<NodeEntity> nodeEntityList) {
        if(nodeEntityList == null){
            return null;
        }
        List<NodeDto> nodeList = new ArrayList<>();
        for(NodeEntity nodeEntity:nodeEntityList){
            nodeList.add(classCast(nodeEntity));
        }
        return nodeList;
    }

    private NodeDto classCast(NodeEntity nodeEntity) {
        NodeDto node = new NodeDto();
        node.setIp(nodeEntity.getIp());
        node.setIsNodeAvailable(nodeEntity.getIsNodeAvailable());
        node.setErrorConnectionTimes(nodeEntity.getErrorConnectionTimes());
        node.setBlockchainHeight(nodeEntity.getBlockchainHeight());
        node.setFork(nodeEntity.getFork());
        return node;
    }

    private NodeEntity classCast(NodeDto node) {
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
