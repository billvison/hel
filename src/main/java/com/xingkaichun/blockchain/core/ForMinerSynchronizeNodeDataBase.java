package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.BlockChainSegement;


/**
 * TODO 多个区块链可以同时写入，能区分节点，要标记某个节点的数据是否同步完成，这是一个标识，提示别人可以来处理这一部分数据了，
 */
public interface ForMinerSynchronizeNodeDataBase {

    boolean addBlockChainSegement(String nodeId,BlockChainSegement blockChainSegement) throws Exception ;

    BlockChainSegement getNextBlockChainSegement(String nodeId) throws Exception ;

    /**
     * 删除节点(nodeId)的同步信息，nodeId不可用了。
     * @param nodeId
     * @throws Exception
     */
    void deleteSynchronizeDataByNodeId(String nodeId) throws Exception ;

    /**
     * 获取一个可用于同步的节点ID
     * @return
     */
    String getAvailableSynchronizeNodeId() throws Exception ;

    /**
     * 设置一个节点ID可用于同步，它的意义是让nodeId准备好数据。
     * @param nodeId
     * @throws Exception
     */
    void setNodeIdAvailableSynchronize(String nodeId) throws Exception ;
}
