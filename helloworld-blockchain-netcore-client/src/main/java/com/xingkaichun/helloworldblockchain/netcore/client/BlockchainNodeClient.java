package com.xingkaichun.helloworldblockchain.netcore.client;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;

/**
 * 区块链节点客户端
 * 向目标节点请求、提交数据
 *
 * @author 邢开春 409060350@qq.com
 */
public interface BlockchainNodeClient {

    /**
     * 提交交易至节点
     */
    PostTransactionResponse postTransaction(PostTransactionRequest request);

    /**
     * Ping节点
     */
    PingResponse pingNode(PingRequest request);

    /**
     * 根据区块高度，获取区块
     */
    GetBlockResponse getBlock(GetBlockRequest request);

    /**
     * 获取节点列表
     */
    GetNodesResponse getNodes(GetNodesRequest request);

    /**
     * 提交区块至节点
     */
    PostBlockResponse postBlock(PostBlockRequest request);

    /**
     * 提交区块链高度至节点
     */
    PostBlockchainHeightResponse postBlockchainHeight(PostBlockchainHeightRequest request);

    /**
     * 获取区块链高度
     */
    GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request);

    /**
     * 根据交易高度，获取交易
     */
    GetTransactionResponse getTransaction(GetTransactionRequest getTransactionRequest);
}
