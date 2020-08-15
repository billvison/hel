package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.page.PageCondition;
import com.xingkaichun.helloworldblockchain.netcore.dto.transaction.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.transaction.SubmitNormalTransactionResultDto;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块链core service
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface BlockChainCoreService {

    /**
     * 根据交易哈希获取交易
     */
    TransactionDTO queryTransactionDtoByTransactionHash(String transactionHash) ;
    /**
     * 根据交易高度获取交易
     */
    List<Transaction> queryTransactionByTransactionHeight(PageCondition pageCondition) ;
    /**
     * 根据地址获取UTXO
     */
    List<TransactionOutput> queryUtxoListByAddress(String address,PageCondition pageCondition) ;
    /**
     * 根据地址获取TXO
     */
    List<TransactionOutput> queryTxoListByAddress(String address,PageCondition pageCondition) ;
    /**
     * 提交交易到区块链网络
     */
    SubmitNormalTransactionResultDto submitTransaction(NormalTransactionDto transactionDTO) ;

    /**
     * 根据区块高度获取区块DTO
     */
    BlockDTO queryBlockDtoByBlockHeight(BigInteger blockHeight) ;
    /**
     * 根据区块哈希获取区块
     */
    Block queryNoTransactionBlockDtoByBlockHash(String blockHash) ;
    /**
     * 根据区块高度获取区块DTO
     */
    Block queryNoTransactionBlockDtoByBlockHeight(BigInteger blockHeight) ;
    /**
     * 根据区块高度获取区块Hash
     */
    String queryBlockHashByBlockHeight(BigInteger blockHeight) ;
    /**
     * 获取区块链高度
     */
    BigInteger queryBlockChainHeight() ;
    /**
     * 查询挖矿中的交易
     */
    List<TransactionDTO> queryMiningTransactionList(PageCondition pageCondition) ;

    TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) ;

    void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) ;

    /**
     * 保存交易到矿工交易数据库
     */
    void saveTransactionToMinerTransactionDatabase(TransactionDTO transactionDTO)  ;
}
