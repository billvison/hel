package com.xingkaichun.helloworldblockchain.node.dto.blockchain;

public class BlockChainApiRoute {

    public static final String IS_MINE_ACTIVE = "/Api/BlockChain/IsMineActive";
    public static final String START_MINE = "/Api/BlockChain/StartMine";
    public static final String STOP_MINE = "/Api/BlockChain/StopMine";

    public static final String GENERATE_WALLETDTO = "/Api/BlockChain/GenerateWalletDTO";
    public static final String SUBMIT_TRANSACTION = "/Api/BlockChain/SubmitTransaction";

    public static final String Query_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockDtoByBlockHeight";
    public static final String Query_BLOCK_HASH_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockHashByBlockHeight";
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_UUID = "/Api/BlockChain/QueryTransactionByTransactionUUID";
    //TODO 合并接口、优化这个其实应该分页查询
    public static final String QUERY_UTXOS_BY_ADDRESS = "/Api/BlockChain/QueryUtxosByAddress";
    public static final String QUERY_TXOS_BY_ADDRESS = "/Api/BlockChain/QueryTxosByAddress";
}
