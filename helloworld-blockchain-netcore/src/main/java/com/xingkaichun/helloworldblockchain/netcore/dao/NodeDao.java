package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.po.NodePO;

import java.util.List;

/**
 * 节点dao
 * 管理（增删改查）已知的网络节点。
 *
 * @author 邢开春 409060350@qq.com
 */
public interface NodeDao {

    /**
     * 查询节点
     */
    NodePO queryNode(String ip);
    /**
     * 查询所有节点
     */
    List<NodePO> queryAllNodeList();
    /**
     * 添加节点
     */
    void addNode(NodePO node);
    /**
     * 更新节点信息
     */
    void updateNode(NodePO node);
    /**
     * 删除节点
     */
    void deleteNode(String ip);
}
