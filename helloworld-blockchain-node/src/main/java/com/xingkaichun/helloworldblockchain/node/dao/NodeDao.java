package com.xingkaichun.helloworldblockchain.node.dao;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.model.NodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface NodeDao {

    /**
     * 查询节点
     */
    NodeEntity queryNode(SimpleNode simpleNode);
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
    int updateNode(NodeEntity node);
    /**
     * 删除节点
     */
    boolean deleteNode(SimpleNode simpleNode);

    /**
     * 查询所有节点
     */
    List<NodeEntity> queryAllNodeList();
}
