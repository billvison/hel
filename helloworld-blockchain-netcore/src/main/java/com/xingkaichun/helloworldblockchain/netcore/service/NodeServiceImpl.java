package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.po.NodePO;
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
        List<NodePO> nodePOList = nodeDao.queryAllNodeList();
        return nodeEntityConvertNode(nodePOList);
    }

    @Override
    public void addNode(Node node){
        if(queryNode(node.getIp()) != null){
            return;
        }
        NodePO nodePO = new NodePO();
        nodePO.setIp(node.getIp());
        long blockchainHeight = node.getBlockchainHeight()!=null?node.getBlockchainHeight():GlobalSetting.GenesisBlock.HEIGHT;
        nodePO.setBlockchainHeight(blockchainHeight);
        nodeDao.addNode(nodePO);
    }

    @Override
    public void updateNode(Node node){
        NodePO nodePO = nodeDao.queryNode(node.getIp());
        if(nodePO == null){
            return;
        }
        if(node.getBlockchainHeight() != null){
            nodePO.setBlockchainHeight(node.getBlockchainHeight());
        }
        nodeDao.updateNode(nodePO);
    }

    @Override
    public Node queryNode(String ip){
        NodePO nodePO = nodeDao.queryNode(ip);
        return nodeEntityConvertNode(nodePO);
    }

    private List<Node> nodeEntityConvertNode(List<NodePO> nodePOList){
        List<Node> nodeList = new ArrayList<>();
        if(nodePOList != null){
            for(NodePO nodePO : nodePOList){
                Node node = nodeEntityConvertNode(nodePO);
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    private Node nodeEntityConvertNode(NodePO nodePO){
        if(nodePO == null){
            return null;
        }
        Node node = new Node();
        node.setIp(nodePO.getIp());
        node.setBlockchainHeight(nodePO.getBlockchainHeight());
        return node;
    }
}
