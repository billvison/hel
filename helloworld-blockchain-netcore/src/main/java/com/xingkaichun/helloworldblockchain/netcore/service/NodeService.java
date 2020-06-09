package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.request.QueryNodeListRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.SimpleNode;

import java.util.List;

/**
 * 节点service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
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
     * 节点网络连接错误处理
     */
    void nodeErrorConnectionHandle(SimpleNode node) ;

    /**
     * 设置节点为分叉节点
     */
    void addOrUpdateNodeForkPropertity(SimpleNode node);

    /**
     * 删除节点
     */
    void deleteNode(SimpleNode node);

    /**
     * 查询节点
     */
    List<Node> queryNodeList(QueryNodeListRequest request);

    /**
     * 新增节点
     */
    void addNode(Node node);

    /**
     * 更新节点
     */
    void updateNode(Node node);
}
