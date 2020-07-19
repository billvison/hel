package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.SimpleNodeDto;

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
    NodeDto queryNode(SimpleNodeDto node);
    /**
     * 获取所有节点
     */
    List<NodeDto> queryAllNoForkNodeList();
    /**
     * 获取所有活着的节点
     */
    List<NodeDto> queryAllNoForkAliveNodeList();
    /**
     * 节点网络连接错误处理
     */
    void nodeErrorConnectionHandle(SimpleNodeDto node);

    /**
     * 设置节点为分叉节点
     */
    void addOrUpdateNodeForkPropertity(SimpleNodeDto node);

    /**
     * 删除节点
     */
    void deleteNode(SimpleNodeDto node);

    /**
     * 查询节点
     */
    List<NodeDto> queryAllNodeList();

    /**
     * 新增节点
     */
    void addNode(NodeDto node);

    /**
     * 更新节点
     */
    void updateNode(NodeDto node);
}
