package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.NodeDTO;

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
    NodeEntity queryNode(NodeDTO node);
    /**
     * 获取所有节点
     */
    List<NodeEntity> queryAllNodeList();

    /**
     * 删除节点
     */
    void deleteNode(NodeDTO node);

    /**
     * 新增节点
     */
    void addNode(NodeEntity node);

    /**
     * 更新节点
     */
    void updateNode(NodeEntity node);
}
