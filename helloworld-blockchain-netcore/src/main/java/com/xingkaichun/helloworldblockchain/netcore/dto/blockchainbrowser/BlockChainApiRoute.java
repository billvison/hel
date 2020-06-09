package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainApiRoute {

    public static final String GENERATE_WALLETDTO = "/Api/BlockChain/GenerateWalletDTO";
    public static final String SUBMIT_TRANSACTION = "/Api/BlockChain/SubmitTransaction";
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockChain/QueryTransactionByTransactionHash";
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HEIGHT = "/Api/BlockChain/QueryTransactionByTransactionHeight";
    public static final String QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockChain/QueryMiningTransactionByTransactionHash";
    public static final String QUERY_UTXOS_BY_ADDRESS = "/Api/BlockChain/QueryUtxosByAddress";
    public static final String QUERY_TXOS_BY_ADDRESS = "/Api/BlockChain/QueryTxosByAddress";
    public static final String PING = "/Api/BlockChain/Ping";
    public static final String QUERY_MINING_TRANSACTION_LIST = "/Api/BlockChain/QueryMiningTransactionList";
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockDtoByBlockHeight";
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HASH = "/Api/BlockChain/QueryBlockDtoByBlockHash";

    public static final String QUERY_BLOCKCHAINBRANCH = "/Api/BlockChain/QueryBlockchainBranch";
}
