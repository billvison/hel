package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDTO;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDTO;

import java.util.List;

/**
 * 节点service
 *
 * @author 邢开春 409060350@qq.com
 */
public interface NodeService {
    /**
     * 查询node
     */
    NodeDTO queryNode(BaseNodeDTO node);
    /**
     * 获取所有节点
     */
    List<NodeDTO> queryAllNodeList();
    /**
     * 获取所有未分叉节点
     */
    List<NodeDTO> queryAllNoForkNodeList();
    /**
     * 获取所有未分叉、活着的节点
     */
    List<NodeDTO> queryAllNoForkAliveNodeList();

    /**
     * 节点网络连接错误处理
     */
    void nodeConnectionErrorHandle(BaseNodeDTO node);

    /**
     * 设置节点为分叉节点
     */
    void setNodeFork(BaseNodeDTO node);

    /**
     * 删除节点
     */
    void deleteNode(BaseNodeDTO node);

    /**
     * 新增节点
     */
    void addNode(NodeDTO node);

    /**
     * 更新节点
     */
    void updateNode(NodeDTO node);
}
