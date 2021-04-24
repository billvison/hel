package com.xingkaichun.helloworldblockchain.netcore.client;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

/**
 * 区块链节点客户端service
 * 向其它节点请求、提交数据
 * @author 邢开春 409060350@qq.com
 */
public interface BlockchainNodeClient {

    /**
     * 提交交易至节点
     */
    String postTransaction(TransactionDTO transactionDTO);

    /**
     * Ping指定节点
     */
    String pingNode();

    /**
     * 根据区块高度，获取对应的区块
     */
    BlockDTO getBlock(long blockHeight);

    /**
     * 获取区块列表
     */
    String[] getNodes();

    /**
     * 提交区块至节点
     */
    String postBlock(BlockDTO blockDTO);

    /**
     * 提交区块链高度至节点
     */
    String postBlockchainHeight(long blockchainHeight);

    /**
     * 获取区块链高度
     */
    Long getBlockchainHeight();

    /**
     * 根据交易高度，获取对应的交易
     */
    TransactionDTO getTransaction(long transactionHeight);
}
