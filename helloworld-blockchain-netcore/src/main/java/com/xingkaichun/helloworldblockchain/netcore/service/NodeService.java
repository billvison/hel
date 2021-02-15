package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDto;

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
    NodeDto queryNode(BaseNodeDto node);
    /**
     * 获取所有节点
     */
    List<NodeDto> queryAllNodeList();
    /**
     * 获取所有未分叉节点
     */
    List<NodeDto> queryAllNoForkNodeList();
    /**
     * 获取所有未分叉、活着的节点
     */
    List<NodeDto> queryAllNoForkAliveNodeList();

    /**
     * 节点网络连接错误处理
     */
    void nodeConnectionErrorHandle(BaseNodeDto node);

    /**
     * 设置节点为分叉节点
     */
    void setNodeFork(BaseNodeDto node);

    /**
     * 删除节点
     */
    void deleteNode(BaseNodeDto node);

    /**
     * 新增节点
     */
    void addNode(NodeDto node);

    /**
     * 更新节点
     */
    void updateNode(NodeDto node);
}
