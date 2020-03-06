package com.xingkaichun.helloworldblockchain.node.dto.blockchain.request;

import lombok.Data;

@Data
public class QueryTransactionByTransactionUuidRequest {

    private String transactionUUID;
}
