package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.pay.Recipient;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

import java.util.List;

/**
 * 单机版[没有网络交互版本]区块链核心，代表一个完整的单机版区块链核心系统。
 * 单机版区块链核心系统，由以下几部分组成：
 * 区块链数据库：用于持久化本地区块链的数据
 * @see BlockchainDatabase
 * 矿工：可以收集交易，挖矿，将新挖取的矿放进区块链数据库
 * @see com.xingkaichun.helloworldblockchain.core.Miner
 * 区块链同步器：区块链是一个分布式的数据库，同步器可以同步其它节点的区块链数据。
 * @see com.xingkaichun.helloworldblockchain.core.Synchronizer
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class BlockchainCore {

    //区块链数据库
    protected BlockchainDatabase blockchainDataBase ;
    //矿工
    protected Miner miner ;
    //钱包
    protected Wallet wallet ;
    //区块链同步器
    protected Synchronizer synchronizer ;

    public BlockchainCore(BlockchainDatabase blockchainDataBase, Wallet wallet, Miner miner, Synchronizer synchronizer) {
        this.blockchainDataBase = blockchainDataBase;
        this.wallet = wallet;
        this.miner = miner;
        this.synchronizer = synchronizer;
    }

    /**
     * 激活区块链核心。激活矿工、激活区块链同步器。
     */
    public abstract void start();



    /**
     * 获取区块链高度
     */
    public abstract long queryBlockchainHeight() ;
    /**
     * 删除区块高度大于等于@blockHeight@的区块
     */
    public abstract void deleteBlocksUtilBlockHeightLessThan(long blockHeight) ;
    /**
     * 根据区块高度获取区块Hash
     */
    public abstract String queryBlockHashByBlockHeight(long blockHeight) ;



    /**
     * 根据区块高度查询区块
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight);
    /**
     * 根据区块哈希查询区块
     */
    public abstract Block queryBlockByBlockHash(String blockHash);



    /**
     * 根据交易哈希获取交易
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;
    /**
     * 根据交易高度获取交易
     */
    public abstract List<Transaction> queryTransactionListByTransactionHeight(long from,long size) ;
    /**
     * 根据地址查询交易列表。from从0开始。
     */
    public abstract List<Transaction> queryTransactionListByAddress(String address,long from,long size) ;

    /**
     * 根据地址获取[交易输出列表(包含未花费交易输出和已花费交易输出)]。from从0开始。
     */
    public abstract List<TransactionOutput> queryTransactionOutputListByAddress(String address,long from,long size) ;
    /**
     * 根据地址获取[未花费交易输出列表]。from从0开始。
     */
    public abstract List<TransactionOutput> queryUnspendTransactionOutputListByAddress(String address,long from,long size) ;
    /**
     * 根据地址获取[已花费交易输出列表]。from从0开始。
     */
    public abstract List<TransactionOutput> querySpendTransactionOutputListByAddress(String address,long from,long size) ;


    /**
     * 构建交易。使用钱包里的账户。
     */
    public abstract BuildTransactionResponse buildTransactionDTO(BuildTransactionRequest request) ;
    /**
     * 构建交易。使用提供的账户。
     * @param payerPrivateKeyList 付款方私钥列表
     * @param payerChangeAddress 付款方接收找零地址
     * @param recipientList 接受方
     */
    public abstract BuildTransactionResponse buildTransactionDTO(List<String> payerPrivateKeyList, String payerChangeAddress, List<Recipient> recipientList) ;
    /**
     * 提交交易到区块链
     */
    public abstract void submitTransaction(TransactionDTO transactionDTO) ;



    /**
     * 查询挖矿中的交易
     */
    public abstract List<TransactionDTO> queryMiningTransactionList(long from,long size) ;
    /**
     * 根据交易哈希查询挖矿中的交易
     */
    public abstract TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) ;






    //region get set
    public BlockchainDatabase getBlockchainDataBase() {
        return blockchainDataBase;
    }

    public Miner getMiner() {
        return miner;
    }

    public Synchronizer getSynchronizer() {
        return synchronizer;
    }

    public Wallet getWallet() {
        return wallet;
    }

    //endregion
}