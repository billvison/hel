package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;

import java.util.List;

public interface NodeService {

    /**
     * 获取所有节点
     */
    List<Node> queryNodes() ;
    /**
     * 获取所有活着的节点
     */
    List<Node> queryAliveNodes() ;
    /**
     * 新增或者更新节点信息
     */
    boolean addOrUpdateNode(Node node) ;
    /**
     * 删除节点
     */
    void deleteNode(SimpleNode node) ;
    /**
     * 节点网络连接错误处理
     */
    void nodeErrorConnectionHandle(SimpleNode node) ;
    /**
     * 查询node
     */
    Node queryNode(SimpleNode node);
}
