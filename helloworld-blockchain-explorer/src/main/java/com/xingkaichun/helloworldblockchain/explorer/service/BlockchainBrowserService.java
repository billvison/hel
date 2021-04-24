package com.xingkaichun.helloworldblockchain.explorer.service;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.explorer.vo.block.BlockView;
import com.xingkaichun.helloworldblockchain.explorer.vo.transaction.*;

import java.util.List;

/**
 * 区块链浏览器service
 *
 * @author 邢开春 409060350@qq.com
 */
public interface BlockchainBrowserService {

    /**
     * 根据交易输出ID获取交易输出
     */
    TransactionOutputDetailView queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId);
    /**
     * 根据地址获取交易输出
     */
    TransactionOutputDetailView queryTransactionOutputByAddress(String address);

    /**
     * 根据交易哈希查询交易
     */
    TransactionView queryTransactionByTransactionHash(String transactionHash);
    /**
     * 根据区块哈希与交易高度查询交易列表
     */
    List<TransactionView> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size);

    /**
     * 提交交易到区块链网络
     */
    SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request);

    /**
     * 根据区块哈希查找区块
     */
    BlockView queryBlockViewByBlockHeight(Long blockHeight);

    /**
     * 根据区块哈希查找未确认的交易
     */
    MiningTransactionView queryMiningTransactionByTransactionHash(String transactionHash);
}
