package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;


/**
 * 该类用于组织区块链数据的存储，并不会对数据进行校验。
 */
public interface BlockChainDataBase {

    //region 区块增加与删除
    /**
     * 新增区块: 在不允许删除区块链上的区块的情况下，将一个新的区块添加到区块链上。
     * 这是一个有些复杂的操作，需要考虑如下几点:
     * 新增区块本身的数据的正确性;
     * 新增的区块是否能够正确衔接到区块链的尾部;
     */
    boolean addBlock(Block block) throws Exception ;

    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    Block removeTailBlock() throws Exception ;
    //endregion

    //region 区块链提供的通用方法
    /**
     * 查找区块链上的最后一个区块
     */
    Block findTailBlock() throws Exception ;

    /**
     * 在区块链中根据 UTXO ID 查找UTXO
     * @param transactionOutputUUID UTXO ID
     */
    TransactionOutput findUtxoByUtxoUuid(String transactionOutputUUID) throws Exception ;

    /**
     * 在区块链中根据区块高度查找区块
     * @param blockHeight 区块高度
     */
    Block findBlockByBlockHeight(int blockHeight) throws Exception ;

    /**
     * 在区块链中根据交易ID查找交易
     * @param transactionUUID 交易ID
     */
    Transaction findTransactionByTransactionUuid(String transactionUUID) throws Exception ;
    //endregion



    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    boolean isBlockCanApplyToBlockChain(Block block) throws Exception ;
    /**
     * 校验(未打包进区块链的)交易的合法性
     * 奖励交易校验需要传入block参数
     */
    public boolean checkUnBlockChainTransaction(Block block, Transaction transaction) throws Exception ;
}