package com.xingkaichun.helloworldblockchain.explorer.vo;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainApiRoute {

    //查询区块链高度
    public static final String QUERY_BLOCKCHAIN_HEIGHT = "/Api/Blockchain/QueryBlockchainHeight";
    //生成账户(公钥、私钥、地址)
    public static final String GENERATE_ACCOUNT = "/Api/Blockchain/GenerateAccount";

    //提交交易到区块链网络
    public static final String SUBMIT_TRANSACTION_TO_BLOCKCHIAINNEWWORK = "/Api/Blockchain/SubmitTransactionToBlockchainNetwork";


    //根据交易哈希查询交易
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HASH = "/Api/Blockchain/QueryTransactionByTransactionHash";
    //根据区块哈希与交易高度查询交易列表
    public static final String QUERY_TRANSACTION_LIST_BY_BLOCK_HASH_TRANSACTION_HEIGHT = "/Api/Blockchain/QueryTransactionListByBlockHashTransactionHeight";
    //根据地址查询账户详情
    public static final String QUERY_ACCOUNT_DETAIL_BY_ADDRESS = "/Api/Blockchain/QueryAccountDetailByAddress";


    //根据地址获取交易输出
    public static final String QUERY_TRANSACTION_OUTPUT_LIST_BY_ADDRESS = "/Api/Blockchain/QueryTransactionOutputListByAddress";

    //根据交易输出ID获取交易输出
    public static final String QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID = "/Api/Blockchain/QueryTransactionOutputByTransactionOutputId";


    //查询挖矿中的交易
    public static final String QUERY_MINING_TRANSACTION_LIST = "/Api/Blockchain/QueryMiningTransactionList";
    //根据交易哈希查询挖矿中交易
    public static final String QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH = "/Api/Blockchain/QueryMiningTransactionByTransactionHash";


    //根据区块高度查询区块
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/Blockchain/QueryBlockDtoByBlockHeight";
    //根据区块哈希查询区块
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HASH = "/Api/Blockchain/QueryBlockDtoByBlockHash";
    //查询最近的10个区块
    public static final String QUERY_LAST10_BLOCKDTO = "/Api/Blockchain/QueryLast10BlockDto";
}
