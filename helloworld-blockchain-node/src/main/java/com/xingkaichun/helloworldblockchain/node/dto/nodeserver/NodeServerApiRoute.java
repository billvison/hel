package com.xingkaichun.helloworldblockchain.node.dto.nodeserver;

public class NodeServerApiRoute {

    public static final String PING = "/Api/NodeServer/Ping";
    public static final String ADD_OR_UPDATE_NODE = "/Api/NodeServer/AddOrUpdateNode";
    public static final String QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT = "/Api/NodeServer/QueryBlockHashByBlockHeight";
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/NodeServer/QueryBlockDtoByBlockHeight";

    public static final String RECEIVE_TRANSACTION = "/Api/NodeServer/ReceiveTransaction";
}
