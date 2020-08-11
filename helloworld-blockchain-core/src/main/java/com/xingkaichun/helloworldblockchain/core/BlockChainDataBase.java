package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;

import java.math.BigInteger;
import java.util.List;


/**
 * 区块链数据库：该类用于区块链数据的持久化。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public abstract class BlockChainDataBase {

    //区块共识
    protected Consensus consensus ;
    //矿工激励
    protected Incentive incentive ;

    public BlockChainDataBase(Consensus consensus,Incentive incentive) {
        this.consensus = consensus;
        this.incentive = incentive;
    }

    //region 区块增加与删除
    /**
     * 新增区块: 在不允许删除区块链上的区块的情况下，将一个新的区块添加到区块链上。
     * 这是一个有些复杂的操作，需要考虑如下几点:
     * 新增区块本身的数据的正确性;
     * 新增的区块是否能够正确衔接到区块链的尾部;
     */
    public abstract boolean addBlock(Block block) throws Exception ;

    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    public abstract Block removeTailBlock() throws Exception ;

    /**
     * 删除区块高度大于等于@blockHeight的的区块
     */
    public abstract void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) throws Exception ;
    //endregion



    //region 校验区块、交易
    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public abstract boolean isBlockCanApplyToBlockChain(Block block) throws Exception ;

    /**
     * 校验交易是否可以被添加进下一个区块之中。
     * 如果校验的是奖励交易，则需要整个区块的信息，因此这个函数包含了两个参数：交易所在的区块、交易
     */
    public abstract boolean isTransactionCanAddToNextBlock(Block block, Transaction transaction) throws Exception ;
    //endregion








    //region 区块链提供的通用方法
    /**
     * 查询区块链的长度
     */
    public abstract BigInteger queryBlockChainHeight() ;
    /**
     * 查询区块链中总的交易数量
     */
    public abstract BigInteger queryTransactionSize() ;

    /**
     * 查询区块链上的最后一个区块
     */
    public abstract Block queryTailBlock() throws Exception ;
    /**
     * 在区块链中根据区块高度查找区块
     * @param blockHeight 区块高度
     */
    public abstract Block queryBlockByBlockHeight(BigInteger blockHeight) throws Exception ;
    /**
     * 查找区块链上的最后一个区块，返回的区块不包含交易信息
     */
    public abstract Block queryTailNoTransactionBlock() throws Exception ;
    /**
     * 在区块链中根据区块高度查找【未存储交易信息】的区块
     * @param blockHeight 区块高度
     */
    public abstract Block queryNoTransactionBlockByBlockHeight(BigInteger blockHeight) throws Exception ;


    /**
     * 在区块链中根据交易ID查找交易
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) throws Exception ;
    /**
     * 根据交易高度查询交易
     */
    public abstract List<Transaction> queryTransactionByTransactionHeight(BigInteger from,BigInteger size) throws Exception ;


    /**
     * 在区块链中根据 交易输出哈希 查找未花费交易输出
     */
    public abstract TransactionOutput queryUnspendTransactionOutputByTransactionOuputHash(String transactionOutputHash) throws Exception ;
    /**
     * 根据地址查询未花费交易输出
     */
    public abstract List<TransactionOutput> queryUnspendTransactionOutputListByAddress(String address,long from,long size) throws Exception ;


    /**
     * 根据地址查询交易输出
     */
    public abstract List<TransactionOutput> queryTransactionOutputListByAddress(String address,long from,long size) throws Exception ;


    /**
     * 根据区块Hash查找区块高度
     * @param blockHash 区块Hash
     */
    public abstract BigInteger queryBlockHeightByBlockHash(String blockHash) ;
    //endregion




    //region get set
    public Incentive getIncentive() {
        return incentive;
    }

    public Consensus getConsensus() {
        return consensus;
    }
    //endregion
}