package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.node.transport.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.response.QueryBlockHashByBlockHeightResponse;

import java.math.BigInteger;

public interface BlockchainNodeClientService {

    /**
     * 提交交易至其它节点
     */
    ServiceResult<EmptyResponse> sumiteTransaction(SimpleNode node, TransactionDTO transactionDTO) throws Exception ;

    /**
     * Ping指定节点
     */
    ServiceResult<PingResponse> pingNode(SimpleNode node) ;

    /**
     * 单播：将本地区块链高度传给指定节点
     */
    ServiceResult<EmptyResponse> unicastLocalBlockChainHeight(SimpleNode node, BigInteger localBlockChainHeight) ;

    /**
     * 根据区块高度，获取对应的区块hash
     */
    ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(Node node, BigInteger blockHeight);

    /**
     * 根据区块高度，获取对应的区块
     */
    ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(Node node, BigInteger blockHeight) ;

}
