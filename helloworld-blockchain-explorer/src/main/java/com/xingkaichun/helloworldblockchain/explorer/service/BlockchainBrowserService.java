package com.xingkaichun.helloworldblockchain.explorer.service;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.explorer.dto.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.xingkaichun.helloworldblockchain.explorer.dto.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.xingkaichun.helloworldblockchain.explorer.dto.transaction.TransactionOutputDetailView;
import com.xingkaichun.helloworldblockchain.explorer.dto.transaction.TransactionView;

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
    List<TransactionOutputDetailView> queryTransactionOutputListByAddress(String address, long from, long size);
    /**
     * 根据地址获取未花费交易输出
     */
    List<TransactionOutputDetailView> queryUnspendTransactionOutputListByAddress(String address, long from, long size);
    /**
     * 根据地址获取已花费交易输出
     */
    List<TransactionOutputDetailView> querySpendTransactionOutputListByAddress(String address, long from, long size);

    /**
     * 根据交易哈希查询交易
     */
    TransactionView queryTransactionByTransactionHash(String transactionHash);
    /**
     * 根据地址查询交易列表
     */
    List<TransactionView> queryTransactionListByAddress(String address, long from, long size);
    /**
     * 根据区块哈希与交易高度查询交易列表
     */
    List<TransactionView> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size);

    /**
     * 提交交易到区块链网络
     */
    SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request);
}
