package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.QueryMiningTransactionListRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.QueryTxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.QueryUtxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response.SubmitNormalTransactionResponse;
import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;
import com.xingkaichun.helloworldblockchain.node.dto.wallet.WalletDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

import java.math.BigInteger;
import java.util.List;

public interface BlockChainCoreService {

    /**
     * 生成钱包
     */
    WalletDTO generateWalletDTO();
    /**
     * 根据交易哈希获取交易
     */
    TransactionDTO queryTransactionDtoByTransactionHash(String transactionHash) throws Exception;
    /**
     * 根据交易高度获取交易
     */
    List<Transaction> queryTransactionByTransactionHeight(PageCondition pageCondition) throws Exception;
    /**
     * 根据地址获取UTXO
     */
    List<TransactionOutput> queryUtxoListByAddress(QueryUtxosByAddressRequest request) throws Exception;
    /**
     * 根据地址获取TXO
     */
    List<TransactionOutput> queryTxoListByAddress(QueryTxosByAddressRequest request) throws Exception;
    /**
     * 提交交易到区块链网络
     */
    SubmitNormalTransactionResponse sumiteTransaction(NormalTransactionDto transactionDTO) throws Exception;

    /**
     * 根据区块高度获取区块DTO
     */
    BlockDTO queryBlockDtoByBlockHeight(BigInteger blockHeight) throws Exception;
    /**
     * 根据区块哈希获取区块
     */
    Block queryNoTransactionBlockDtoByBlockHash(String blockHash) throws Exception;
    /**
     * 根据区块高度获取区块DTO
     */
    Block queryNoTransactionBlockDtoByBlockHeight(BigInteger blockHeight) throws Exception;
    /**
     * 根据区块高度获取区块Hash
     */
    String queryBlockHashByBlockHeight(BigInteger blockHeight) throws Exception;
    /**
     * 获取区块链高度
     */
    BigInteger queryBlockChainHeight() throws Exception;
    /**
     * 查询挖矿中的交易
     */
    List<TransactionDTO> queryMiningTransactionList(QueryMiningTransactionListRequest request) throws Exception;

    TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) throws Exception;

    void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) throws Exception;
}
