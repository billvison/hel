package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.dto.WalletDTO;
import com.xingkaichun.helloworldblockchain.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;

import java.util.List;

public interface BlockChainService {

    /**
     * 生成钱包
     */
    WalletDTO generateWalletDTO();
    /**
     * 根据交易UUID获取交易
     */
    TransactionDTO QueryTransactionDtoByTransactionUUID(String transactionUUID) throws Exception;
    /**
     * 根据地址获取UTXO
     */
    List<TransactionOutput> queryUtxoListByAddress(String address) throws Exception;
    /**
     * 根据地址获取TXO
     */
    List<TransactionOutput> queryTxoListByAddress(String address) throws Exception;
    /**
     * 提交交易到区块链网络
     */
    TransactionDTO sumiteTransaction(NormalTransactionDto transactionDTO) throws Exception;
    /**
     * 给打上签名交易
     */
    TransactionDTO signatureTransactionDTO(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) throws Exception;

    /**
     * 根据区块高度获取区块DTO
     */
    BlockDTO queryBlockDtoByBlockHeight(int blockHeight) throws Exception;
    /**
     * 根据区块高度获取区块Hash
     */
    String queryBlockHashByBlockHeight(int blockHeight) throws Exception;
    /**
     * 获取区块链高度
     */
    int queryBlockChainHeight() throws Exception;
    /**
     * 查询挖矿中的交易
     */
    List<TransactionDTO> queryMiningTransactionList() throws Exception;

    TransactionDTO queryMiningTransactionDtoByTransactionUUID(String transactionUUID) throws Exception;
}
