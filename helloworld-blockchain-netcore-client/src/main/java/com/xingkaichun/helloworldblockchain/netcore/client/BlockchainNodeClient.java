package com.xingkaichun.helloworldblockchain.netcore.client;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PingRequest;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PingResponse;
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
    String submitTransaction(TransactionDTO transactionDTO) ;

    /**
     * Ping指定节点
     */
    PingResponse pingNode(PingRequest request) ;

    /**
     * 根据区块高度，获取对应的区块
     */
    BlockDTO getBlock(long blockHeight) ;

    /**
     * 提交区块至节点
     */
    String psotBlock(BlockDTO blockDTO);
}
