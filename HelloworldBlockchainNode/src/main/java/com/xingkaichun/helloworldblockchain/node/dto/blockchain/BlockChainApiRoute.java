package com.xingkaichun.helloworldblockchain.node.dto.blockchain;

public class BlockChainApiRoute {

    public static final String GET_BLOCKDTO = "/Api/BlockChain/GetBlockDTO";
    public static final String GET_BLOCK_HASH_BY_BLOCK_HEIGHT = "/Api/BlockChain/GetBlockHashByBlockHeight";
    public static final String STOP_Mine = "/Api/BlockChain/stopMine";
    public static final String START_MINE = "/Api/BlockChain/startMine";
    public static final String GenerateWallet ="/Api/BlockChain/GenerateWallet";
    public static final String SUBMIT_TRANSACTION = "/Api/BlockChain/SubmitTransaction";
    public static final String QUERY_BLOCK_BY_BLOCK_HEIGHT = "/Api/BlockChain/QueryBlockByBlockHeight" ;
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_UUID = "/Api/BlockChain/QueryTransactionByTransactionUUID";
    public static final String QUERY_UTXOS_BY_ADDRESS = "/Api/BlockChain/QueryUtxosByAddress";
}
