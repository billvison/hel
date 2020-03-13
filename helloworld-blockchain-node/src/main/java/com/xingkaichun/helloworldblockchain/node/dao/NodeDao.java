package com.xingkaichun.helloworldblockchain.node.dao;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;

import java.util.List;

public interface NodeDao {

    /**
     * 查询节点
     */
    Node queryNode(String ip, int port);
    /**
     * 获取所有节点
     */
    List<Node> queryAllNodeList();
    /**
     * 获取所有活着的节点
     */
    List<Node> queryAliveNodes();
    /**
     * 添加节点
     */
    void addNode(Node node);
    /**
     * 更新节点信息
     */
    int updateNode(Node node);
    /**
     * 删除节点
     */
    boolean deleteNode(String ip, int port);
}
