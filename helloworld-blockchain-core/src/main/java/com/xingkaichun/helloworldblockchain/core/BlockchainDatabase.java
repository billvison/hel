package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;


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
    //虚拟机
    protected VirtualMachine virtualMachine;

    public BlockchainDatabase(Consensus consensus, Incentive incentive,VirtualMachine virtualMachine) {
        this.consensus = consensus;
        this.incentive = incentive;
        this.virtualMachine = virtualMachine;
    }
    //endregion



    //region 区块增加与删除
    /**
     * 将一个区块添加到区块链的尾部。
     */
    public abstract boolean addBlockDto(BlockDto blockDto) ;
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
     * 只有一种情况，区块可以被添加到区块链，即: 区块是区块链上的下一个区块。
     */
    public abstract boolean checkBlock(Block block) ;
    /**
     * 校验交易是否可以被添加进下一个区块之中。
     * 注意，如果是创世交易，则会跳过激励金额的校验，但除了不校验激励金额外，仍然会校验创世交易的方方面面，如交易大小、交易的结构等。
     * 为什么会跳过创世交易的激励金额的校验？
     * 因为激励金额的校验需要整个区块的信息，因此激励校验是区块层面的校验，而不是在交易层面校验激励金额。
     */
    public abstract boolean checkTransaction(Transaction transaction) ;
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
     * 查询区块链中总的交易输出数量
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
     * 查询来源交易：查询交易输出产生于的那笔交易
     */
    public abstract Transaction querySourceTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    /**
     * 查询去向交易：查询使用交易输出的那笔交易
     */
    public abstract Transaction queryDestinationTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    //endregion



    //region 交易输出查询
    /**
     * 根据 交易输出高度 查找 交易输出
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputHeight(long transactionOutputHeight) ;
    /**
     * 根据 交易输出ID 查找 交易输出
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    /**
     * 根据 交易输出ID 查找 未花费交易输出
     */
    public abstract TransactionOutput queryUnspentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    /**
     * 根据 交易输出ID 查找 已花费交易输出
     */
    public abstract TransactionOutput querySpentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
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