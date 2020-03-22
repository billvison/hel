package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.QueryBlockHashByBlockHeightResponse;

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
    ServiceResult<EmptyResponse> unicastLocalBlockChainHeight(SimpleNode node, int localBlockChainHeight) ;

    ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(Node node, int blockHeight);
}
