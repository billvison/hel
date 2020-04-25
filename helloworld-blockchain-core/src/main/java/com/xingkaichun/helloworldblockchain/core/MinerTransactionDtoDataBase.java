package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

import java.util.List;

/**
 * 矿工交易数据库
 * 该类的作用是收集可能用于挖矿的交易。
 * 所有没有被成功挖矿而放进区块链的交易，都应该尽可能的被收集起来，供挖矿使用。
 * 其它对象可以从本类获取交易数据，然后进行自己的活动。例如矿工可以从该类获取挖矿的原材料(交易数据)进行挖矿活动。
 */
public abstract class  MinerTransactionDtoDataBase {

    /**
     * 新增交易
     */
    public abstract void insertTransactionDTO(TransactionDTO transactionDTO) throws Exception ;

    /**
     * 新增交易
     */
    public abstract void insertTransactionDtoList(List<TransactionDTO> transactionDTOList) throws Exception ;

    /**
     * 获取交易
     */
    public abstract List<TransactionDTO> selectTransactionDtoList(BlockChainDataBase blockChainDataBase,long from, long size) throws Exception ;

    /**
     * 删除交易
     */
    public abstract void deleteTransactionDtoByTransactionHash(String transactionHash) throws Exception ;

    /**
     * 删除交易
     */
    public abstract void deleteTransactionDtoListByTransactionHashList(List<String> transactionHashList) throws Exception ;

    /**
     * 查询交易
     */
    public abstract TransactionDTO selectTransactionDtoByTransactionHash(String transactionHash) throws Exception;
}
