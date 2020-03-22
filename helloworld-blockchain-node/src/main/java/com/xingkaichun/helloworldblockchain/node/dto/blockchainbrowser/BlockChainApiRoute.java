package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser;

public class BlockChainApiRoute {

    public static final String GENERATE_WALLETDTO = "/Api/BlockChain/GenerateWalletDTO";
    public static final String SUBMIT_TRANSACTION = "/Api/BlockChain/SubmitTransaction";
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_UUID = "/Api/BlockChain/QueryTransactionByTransactionUUID";
    public static final String QUERY_MINING_TRANSACTION_BY_TRANSACTION_UUID = "/Api/BlockChain/QueryMiningTransactionByTransactionUUID";
    //TODO 合并接口、优化这个其实应该分页查询
    public static final String QUERY_UTXOS_BY_ADDRESS = "/Api/BlockChain/QueryUtxosByAddress";
    public static final String QUERY_TXOS_BY_ADDRESS = "/Api/BlockChain/QueryTxosByAddress";
    public static final String PING = "/Api/BlockChain/Ping";
    public static final String QUERY_MINING_TRANSACTION_LIST = "/Api/BlockChain/QueryMiningTransactionList";
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockDtoByBlockHeight";

    public static final String QUERY_BLOCKCHAINBRANCH = "/Api/BlockChain/QueryBlockchainBranch";
}
