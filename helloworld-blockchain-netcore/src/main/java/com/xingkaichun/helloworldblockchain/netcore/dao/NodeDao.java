package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.model.NodeEntity;

import java.util.List;

/**
 * 节点dao
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface NodeDao {

    /**
     * 查询节点
     */
    NodeEntity queryNode(String ip, int port);
    /**
     * 获取所有未分叉节点
     */
    List<NodeEntity> queryAllNoForkNodeList();
    /**
     * 获取所有活着的、未分叉节点
     */
    List<NodeEntity> queryAllNoForkAliveNodeList();
    /**
     * 添加节点
     */
    void addNode(NodeEntity node);
    /**
     * 更新节点信息
     */
    void updateNode(NodeEntity node);
    /**
     * 删除节点
     */
    boolean deleteNode(String ip, int port);

    /**
     * 查询所有节点
     */
    List<NodeEntity> queryAllNodeList();
}
