package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.model.Node;

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
    Node queryNode(String ip);
    /**
     * 获取所有节点
     */
    List<Node> queryAllNodes();

    /**
     * 删除节点
     */
    void deleteNode(String ip);

    /**
     * 新增节点
     */
    void addNode(Node node);

    /**
     * 更新节点
     */
    void updateNode(Node node);
}
