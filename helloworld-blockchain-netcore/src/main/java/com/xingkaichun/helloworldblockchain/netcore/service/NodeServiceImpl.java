package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.utils.LongUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.SimpleNodeDto;
import com.xingkaichun.helloworldblockchain.netcore.model.NodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class NodeServiceImpl implements NodeService {

    private NodeDao nodeDao;
    private ConfigurationService configurationService;

    public NodeServiceImpl(NodeDao nodeDao, ConfigurationService configurationService) {
        this.nodeDao = nodeDao;
        this.configurationService = configurationService;
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
    public void nodeErrorConnectionHandle(SimpleNodeDto simpleNodeDto){
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNodeDto.getIp(), simpleNodeDto.getPort());
        if(nodeEntity == null){
            return;
        }
        int errorConnectionTimes = nodeEntity.getErrorConnectionTimes()+1;
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD.name());
        if(errorConnectionTimes >= Long.parseLong(configurationDto.getConfValue())){
            nodeDao.deleteNode(simpleNodeDto.getIp(), simpleNodeDto.getPort());
        } else {
            nodeEntity.setErrorConnectionTimes(errorConnectionTimes);
            nodeEntity.setIsNodeAvailable(false);
            nodeDao.updateNode(nodeEntity);
        }
    }

    @Override
    public void addOrUpdateNodeForkPropertity(SimpleNodeDto simpleNodeDto){
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNodeDto.getIp(), simpleNodeDto.getPort());
        if(nodeEntity == null){
            NodeDto node = new NodeDto();
            node.setIp(simpleNodeDto.getIp());
            node.setPort(simpleNodeDto.getPort());
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
    public void deleteNode(SimpleNodeDto simpleNodeDto){
        nodeDao.deleteNode(simpleNodeDto.getIp(), simpleNodeDto.getPort());
    }

    @Override
    public List<NodeDto> queryAllNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNodeList();
        List<NodeDto> nodeList = classCast(nodeEntityList);
        return nodeList;
    }

    @Override
    public void addNode(NodeDto node){
        fillNodeDefaultValue(node);
        NodeEntity nodeEntityByPass = classCast(node);
        nodeDao.addNode(nodeEntityByPass);
    }

    @Override
    public void updateNode(NodeDto node){
        NodeEntity nodeEntit = classCast(node);
        nodeDao.updateNode(nodeEntit);
    }

    @Override
    public NodeDto queryNode(SimpleNodeDto simpleNodeDto){
        NodeEntity nodeEntity = nodeDao.queryNode(simpleNodeDto.getIp(), simpleNodeDto.getPort());
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
        node.setPort(nodeEntity.getPort());
        node.setIsNodeAvailable(nodeEntity.getIsNodeAvailable());
        node.setErrorConnectionTimes(nodeEntity.getErrorConnectionTimes());
        node.setBlockChainHeight(nodeEntity.getBlockChainHeight());
        node.setFork(nodeEntity.getFork());
        return node;
    }

    private NodeEntity classCast(NodeDto node) {
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setIp(node.getIp());
        nodeEntity.setPort(node.getPort());
        nodeEntity.setIsNodeAvailable(node.getIsNodeAvailable());
        nodeEntity.setErrorConnectionTimes(node.getErrorConnectionTimes());
        nodeEntity.setBlockChainHeight(node.getBlockChainHeight());
        nodeEntity.setFork(node.getFork());
        return nodeEntity;
    }

    private void fillNodeDefaultValue(NodeDto node) {
        if(node.getBlockChainHeight() == null){
            node.setBlockChainHeight(LongUtil.ZERO);
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
