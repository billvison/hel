package com.xingkaichun.helloworldblockchain.application.vo;

/**
 * 区块链浏览器应用接口
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainBrowserApplicationApi {

    //查询区块链高度
    public static final String QUERY_BLOCKCHAIN_HEIGHT = "/Api/BlockchainBrowserApplication/QueryBlockchainHeight";



    //根据交易哈希查询交易
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockchainBrowserApplication/QueryTransactionByTransactionHash";
    //根据区块哈希与交易高度查询交易列表
    public static final String QUERY_TRANSACTION_LIST_BY_BLOCK_HASH_TRANSACTION_HEIGHT = "/Api/BlockchainBrowserApplication/QueryTransactionListByBlockHashTransactionHeight";


    //根据地址获取交易输出
    public static final String QUERY_TRANSACTION_OUTPUT_BY_ADDRESS = "/Api/BlockchainBrowserApplication/QueryTransactionOutputByAddress";

    //根据交易输出ID获取交易输出
    public static final String QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID = "/Api/BlockchainBrowserApplication/QueryTransactionOutputByTransactionOutputId";


    //查询挖矿中的交易
    public static final String QUERY_MINING_TRANSACTION_LIST = "/Api/BlockchainBrowserApplication/QueryMiningTransactionList";
    //根据交易哈希查询挖矿中交易
    public static final String QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockchainBrowserApplication/QueryMiningTransactionByTransactionHash";


    //根据区块高度查询区块
    public static final String QUERY_BLOCK_BY_BLOCK_HEIGHT = "/Api/BlockchainBrowserApplication/QueryBlockByBlockHeight";
    //根据区块哈希查询区块
    public static final String QUERY_BLOCK_BY_BLOCK_HASH = "/Api/BlockchainBrowserApplication/QueryBlockByBlockHash";
    //查询最近的10个区块
    public static final String QUERY_TOP10_BLOCK = "/Api/BlockchainBrowserApplication/QueryTop10Block";
}
