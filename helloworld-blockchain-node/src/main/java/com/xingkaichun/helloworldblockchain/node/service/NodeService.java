package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;

import java.util.List;

public interface NodeService {
    /**
     * 查询node
     */
    Node queryNode(SimpleNode node);
    /**
     * 获取所有节点
     */
    List<Node> queryAllNoForkNodeList() ;
    /**
     * 获取所有活着的节点
     */
    List<Node> queryAllNoForkAliveNodeList() ;
    
    /**
     * 新增或者更新节点信息 TODO 职责不单一
     */
    boolean addOrUpdateNode(Node node) ;
    /**
     * 节点网络连接错误处理
     */
    void nodeErrorConnectionHandle(SimpleNode node) ;

    /**
     * 设置节点为分叉节点
     */
    void setNodeFork(SimpleNode node);
}
