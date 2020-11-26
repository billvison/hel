package com.xingkaichun.helloworldblockchain.netcore.node.client;

import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.*;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

/**
 * 区块链节点客户端service
 * 向其它节点请求、提交数据
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface BlockchainNodeClient {

    /**
     * 提交交易至节点
     */
    ServiceResult<SubmitTransactionToNodeResponse> submitTransaction(BaseNodeDto node, TransactionDTO transactionDTO) ;

    /**
     * Ping指定节点
     */
    ServiceResult<PingResponse> pingNode(BaseNodeDto node) ;

    /**
     * 单播：将本地区块链高度传给指定节点
     */
    ServiceResult<AddOrUpdateNodeResponse> unicastLocalBlockchainHeight(BaseNodeDto node, long localBlockchainHeight) ;

    /**
     * 根据区块高度，获取对应的区块hash
     */
    ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(NodeDto node, Long blockHeight);

    /**
     * 根据区块高度，获取对应的区块
     */
    ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(NodeDto node, Long blockHeight) ;

}
