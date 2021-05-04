package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
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
    public void deleteNode(String ip){
        nodeDao.deleteNode(ip);
    }

    @Override
    public List<Node> queryAllNodeList(){
        List<NodeEntity> nodeEntityList = nodeDao.queryAllNodeList();
        return nodeEntityConvertNode(nodeEntityList);
    }

    @Override
    public void addNode(Node node){
        if(queryNode(node.getIp()) != null){
            return;
        }
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setIp(node.getIp());
        long blockchainHeight = node.getBlockchainHeight()!=null?node.getBlockchainHeight():GlobalSetting.GenesisBlock.HEIGHT;
        nodeEntity.setBlockchainHeight(blockchainHeight);
        nodeDao.addNode(nodeEntity);
    }

    @Override
    public void updateNode(Node node){
        NodeEntity nodeEntity = nodeDao.queryNode(node.getIp());
        if(nodeEntity == null){
            return;
        }
        if(node.getBlockchainHeight() != null){
            nodeEntity.setBlockchainHeight(node.getBlockchainHeight());
        }
        nodeDao.updateNode(nodeEntity);
    }

    @Override
    public Node queryNode(String ip){
        NodeEntity nodeEntity = nodeDao.queryNode(ip);
        return nodeEntityConvertNode(nodeEntity);
    }

    private List<Node> nodeEntityConvertNode(List<NodeEntity> nodeEntityList){
        List<Node> nodeList = new ArrayList<>();
        if(nodeEntityList != null){
            for(NodeEntity nodeEntity:nodeEntityList){
                Node node = nodeEntityConvertNode(nodeEntity);
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    private Node nodeEntityConvertNode(NodeEntity nodeEntity){
        if(nodeEntity == null){
            return null;
        }
        Node node = new Node();
        node.setIp(nodeEntity.getIp());
        node.setBlockchainHeight(nodeEntity.getBlockchainHeight());
        return node;
    }
}
