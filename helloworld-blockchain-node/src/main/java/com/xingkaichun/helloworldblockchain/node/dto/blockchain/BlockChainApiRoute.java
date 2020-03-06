package com.xingkaichun.helloworldblockchain.node.dto.blockchain;

public class BlockChainApiRoute {

    public static final String IS_MINER_ACTIVE = "/Api/BlockChain/IsMinerActive";
    public static final String ACTIVE_MINER = "/Api/BlockChain/ActiveMiner";
    public static final String DEACTIVE_MINER = "/Api/BlockChain/DeactiveMiner";

    public static final String IS_SYNCHRONIZER_ACTIVE = "/Api/BlockChain/IsSynchronizerActive";
    public static final String ACTIVE_SYNCHRONIZER = "/Api/BlockChain/ActiveSynchronizer";
    public static final String DEACTIVE_SYNCHRONIZER = "/Api/BlockChain/DeactiveSynchronizer";

    public static final String GENERATE_WALLETDTO = "/Api/BlockChain/GenerateWalletDTO";
    public static final String SUBMIT_TRANSACTION = "/Api/BlockChain/SubmitTransaction";

    public static final String Query_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockDtoByBlockHeight";
    public static final String Query_BLOCK_HASH_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockHashByBlockHeight";
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_UUID = "/Api/BlockChain/QueryTransactionByTransactionUUID";
    //TODO 合并接口、优化这个其实应该分页查询
    public static final String QUERY_UTXOS_BY_ADDRESS = "/Api/BlockChain/QueryUtxosByAddress";
    public static final String QUERY_TXOS_BY_ADDRESS = "/Api/BlockChain/QueryTxosByAddress";
}
