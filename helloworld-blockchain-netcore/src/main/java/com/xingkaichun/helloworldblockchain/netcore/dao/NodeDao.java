package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;

import java.util.List;

/**
 * 节点dao
 * 管理（增删改查）已知的网络节点。
 * @author 邢开春
 */
public interface NodeDao {

    /**
     * 查询节点
     */
    NodeEntity queryNode(String ip);
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
    void deleteNode(String ip);
    /**
     * 查询所有节点
     */
    List<NodeEntity> queryAllNodeList();
}
