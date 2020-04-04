package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

@Data
public class QueryMiningTransactionByTransactionUuidRequest {

    private String transactionUUID;
}
