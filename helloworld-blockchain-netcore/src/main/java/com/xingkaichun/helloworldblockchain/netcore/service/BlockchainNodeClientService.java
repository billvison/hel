package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.SimpleNodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockHashByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

import java.math.BigInteger;

/**
 * 区块链节点客户端service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockchainNodeClientService {

    /**
     * 提交交易至其它节点
     */
    ServiceResult<EmptyResponse> sumiteTransaction(SimpleNodeDto node, TransactionDTO transactionDTO) ;

    /**
     * Ping指定节点
     */
    ServiceResult<PingResponse> pingNode(SimpleNodeDto node) ;

    /**
     * 单播：将本地区块链高度传给指定节点
     */
    ServiceResult<EmptyResponse> unicastLocalBlockChainHeight(SimpleNodeDto node, BigInteger localBlockChainHeight) ;

    /**
     * 根据区块高度，获取对应的区块hash
     */
    ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(NodeDto node, BigInteger blockHeight);

    /**
     * 根据区块高度，获取对应的区块
     */
    ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(NodeDto node, BigInteger blockHeight) ;

}
