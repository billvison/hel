package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.SubmitNormalTransactionResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.page.PageCondition;
import com.xingkaichun.helloworldblockchain.netcore.dto.account.AccountDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块链core service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockChainCoreService {

    /**
     * 生成钱包
     */
    AccountDTO generateWalletDTO();
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
    List<TransactionOutput> queryUtxoListByAddress(String address,PageCondition pageCondition) throws Exception;
    /**
     * 根据地址获取TXO
     */
    List<TransactionOutput> queryTxoListByAddress(String address,PageCondition pageCondition) throws Exception;
    /**
     * 提交交易到区块链网络
     */
    SubmitNormalTransactionResult submitTransaction(NormalTransactionDto transactionDTO) throws Exception;

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
    List<TransactionDTO> queryMiningTransactionList(PageCondition pageCondition) throws Exception;

    TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) throws Exception;

    void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) throws Exception;

    /**
     * 保存交易到矿工交易数据库
     */
    void saveTransactionToMinerTransactionDatabase(TransactionDTO transactionDTO) throws Exception ;
}
