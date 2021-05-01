package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;


/**
 * 区块链数据库：该类用于区块链数据的持久化。
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class BlockchainDatabase {

    //region 变量与构造函数
    //区块共识
    protected Consensus consensus ;
    //矿工激励
    protected Incentive incentive ;

    public BlockchainDatabase(Consensus consensus, Incentive incentive) {
        this.consensus = consensus;
        this.incentive = incentive;
    }
    //endregion



    //region 区块增加与删除
    /**
     * 将一个区块添加到区块链的尾部。
     * 这是一个有些复杂的操作，需要考虑如下几点:
     * 新增区块本身的数据的正确性;
     * 新增的区块是否能够正确衔接到区块链的尾部;
     */
    public abstract boolean addBlock(Block block) ;
    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    public abstract void deleteTailBlock() ;
    /**
     * 删除区块高度大于等于@blockHeight@的区块
     */
    public abstract void deleteBlocks(long blockHeight) ;
    //endregion



    //region 校验区块、交易
    /**
     * 检测区块是否可以被添加到区块链上
     * 只有一种情况，区块可以被添加到区块链，即: 区块是区块链上的下一个区块
     */
    public abstract boolean isBlockCanAddToBlockchain(Block block) ;
    /**
     * 校验交易是否可以被添加进下一个区块之中。
     */
    public abstract boolean isTransactionCanAddToNextBlock(Transaction transaction) ;
    //endregion



    //region 区块链查询
    /**
     * 查询区块链的长度
     */
    public abstract long queryBlockchainHeight() ;
    /**
     * 查询区块链中总的交易数量
     */
    public abstract long queryBlockchainTransactionHeight() ;
    /**
     * 查询区块链中总的交易数量
     */
    public abstract long queryBlockchainTransactionOutputHeight() ;
    //endregion



    //region 区块查询
    /**
     * 查询区块链上的最后一个区块
     */
    public abstract Block queryTailBlock() ;
    /**
     * 在区块链中根据区块高度查找区块
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight) ;
    /**
     * 在区块链中根据区块哈希查找区块
     */
    public abstract Block queryBlockByBlockHash(String blockHash) ;
    //endregion



    //region 交易查询
    /**
     * 根据交易高度查询交易。交易高度从1开始。
     */
    public abstract Transaction queryTransactionByTransactionHeight(long transactionHeight) ;
    /**
     * 在区块链中根据交易哈希查找交易
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;
    /**
     * 来源交易
     */
    public abstract Transaction querySourceTransactionByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    /**
     * 去向交易
     */
    public abstract Transaction queryDestinationTransactionByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    //endregion



    //region 交易输出查询
    /**
     * 根据 交易输出高度 查找 交易输出
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputHeight(long transactionOutputHeight) ;
    /**
     * 根据 交易输出ID 查找 交易输出
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    /**
     * 根据 交易输出ID 查找 未花费交易输出
     */
    public abstract TransactionOutput queryUnspentTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    /**
     * 根据 交易输出ID 查找 已花费交易输出
     */
    public abstract TransactionOutput querySpentTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    //endregion



    //region 地址查询
    /**
     * 根据 地址 查询 交易输出
     */
    public abstract TransactionOutput queryTransactionOutputByAddress(String address) ;
    /**
     * 根据 地址 查询 未花费交易输出
     */
    public abstract TransactionOutput queryUnspentTransactionOutputByAddress(String address) ;
    /**
     * 根据 地址 查询 已花费交易输出
     */
    public abstract TransactionOutput querySpentTransactionOutputByAddress(String address);
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