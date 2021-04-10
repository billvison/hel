package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.NodeDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

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
    public void deleteNode(NodeDTO nodeDTO){
        nodeDao.deleteNode(nodeDTO.getIp());
    }

    @Override
    public List<NodeEntity> queryAllNodeList(){
        return nodeDao.queryAllNodeList();
    }

    @Override
    public void addNode(NodeEntity node){
        fillNodeDefaultValue(node);
        nodeDao.addNode(node);
    }

    @Override
    public void updateNode(NodeEntity node){
        nodeDao.updateNode(node);
    }

    @Override
    public NodeEntity queryNode(NodeDTO nodeDTO){
        return nodeDao.queryNode(nodeDTO.getIp());
    }

    private void fillNodeDefaultValue(NodeEntity nodeEntity) {
        if(nodeEntity.getBlockchainHeight() == null){
            nodeEntity.setBlockchainHeight(GlobalSetting.GenesisBlock.HEIGHT);
        }
    }
}
