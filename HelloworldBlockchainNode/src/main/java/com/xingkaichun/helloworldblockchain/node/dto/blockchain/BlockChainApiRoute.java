package com.xingkaichun.helloworldblockchain.node.dto.blockchain;

public class BlockChainApiRoute {

    public static final String STOP_MINE = "/Api/BlockChain/StopMine";
    public static final String START_MINE = "/Api/BlockChain/StartMine";

    public static final String GENERATE_WALLET = "/Api/BlockChain/GenerateWallet";
    public static final String SUBMIT_TRANSACTION = "/Api/BlockChain/SubmitTransaction";

    public static final String Query_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockDtoByBlockHeight";
    public static final String Query_BLOCK_HASH_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockHashByBlockHeight";
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_UUID = "/Api/BlockChain/QueryTransactionByTransactionUUID";
    public static final String QUERY_UTXOS_BY_ADDRESS = "/Api/BlockChain/QueryUtxosByAddress";

}
