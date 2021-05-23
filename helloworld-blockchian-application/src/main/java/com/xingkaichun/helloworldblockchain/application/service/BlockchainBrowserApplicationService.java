package com.xingkaichun.helloworldblockchain.application.service;

import com.xingkaichun.helloworldblockchain.application.vo.block.BlockView;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.MiningTransactionView;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.TransactionOutputDetailView;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.TransactionView;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;

import java.util.List;

/**
 * 区块链浏览器应用service
 *
 * @author 邢开春 409060350@qq.com
 */
public interface BlockchainBrowserApplicationService {

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
     * 根据区块哈希查找区块
     */
    BlockView queryBlockViewByBlockHeight(Long blockHeight);

    /**
     * 根据区块哈希查找未确认的交易
     */
    MiningTransactionView queryMiningTransactionByTransactionHash(String transactionHash);
}
