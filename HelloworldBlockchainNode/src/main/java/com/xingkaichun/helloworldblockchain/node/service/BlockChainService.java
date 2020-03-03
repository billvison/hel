package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.core.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.core.dto.WalletDTO;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryTransactionByTransactionUuidRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryUtxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockDtoByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockHashByBlockHeightRequest;

import java.util.List;

public interface BlockChainService {

    /**
     * 生成钱包
     */
    WalletDTO generateWalletDTO();
    /**
     * 根据交易UUID获取交易
     */
    TransactionDTO QueryTransactionDtoByTransactionUUID(QueryTransactionByTransactionUuidRequest request) throws Exception;
    /**
     * 根据地址获取UTXO
     */
    List<TransactionOutput> queryUtxoListByAddress(QueryUtxosByAddressRequest request) throws Exception;
    /**
     * 提交交易到区块链网络
     */
    TransactionDTO sumiteTransaction(NormalTransactionDto transactionDTO) throws Exception;
    /**
     * 给打上签名交易
     */
    TransactionDTO signatureTransactionDTO(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) throws Exception;
    /**
     * 开始挖矿
     */
    void startMine() throws Exception;
    /**
     * 停止挖矿
     */
    void stopMine() throws Exception;
    /**
     * 根据区块高度获取区块DTO
     */
    BlockDTO queryBlockDtoByBlockHeight(QueryBlockDtoByBlockHeightRequest request) throws Exception;
    /**
     * 根据区块高度获取区块Hash
     */
    String queryBlockHashByBlockHeight(QueryBlockHashByBlockHeightRequest request) throws Exception;
    /**
     * 获取区块链高度
     */
    int queryBlockChainHeight() throws Exception;
}
