package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;

import java.util.List;


/**
 * 区块链
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
     * 删除区块链的尾巴[最后一个]区块
     * //TODO 是否需要按照高度删除区块
     */
    Block removeTailBlock() throws Exception ;

    /**
     * 新增多个区块
     * 这个是一个比较复杂的操作。不仅要考虑每一个区块数据的正确性，
     *
     */
    boolean replaceBlocks(List<Block> addBlockList) throws Exception ;
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

    /**
     * 交易是否已经存在于区块链之中？
     * @param transactionUUID 交易ID
     */
    boolean isTransactionExist(String transactionUUID) throws Exception ;

    /**
     * UUID是否已经存在于区块链之中？
     * @param uuid uuid
     */
    boolean isUuidExist(String uuid) ;
    //endregion
}