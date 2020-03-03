package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.core.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryBlockByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryTransactionByTransactionUuidRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryUtxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.GetBlockHashByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockDtoRequest;

import java.util.List;

public interface BlockChainService {

    /**
     * 生成钱包
     */
    Wallet generateWallet();
    /**
     * 根据区块高度获取区块
     */
    Block queryBlockByBlockHeight(QueryBlockByBlockHeightRequest request) throws Exception;
    /**
     * 根据交易UUID获取交易
     */
    Transaction QueryTransactionByTransactionUUID(QueryTransactionByTransactionUuidRequest request) throws Exception;
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
    BlockDTO queryBlockDtoByBlockHeight(QueryBlockDtoRequest request) throws Exception;
    /**
     * 根据区块高度获取区块Hash
     */
    String queryBlockHashByBlockHeight(GetBlockHashByBlockHeightRequest request) throws Exception;
    /**
     * 获取区块链高度
     */
    int queryBlockChainHeight() throws Exception;
}
