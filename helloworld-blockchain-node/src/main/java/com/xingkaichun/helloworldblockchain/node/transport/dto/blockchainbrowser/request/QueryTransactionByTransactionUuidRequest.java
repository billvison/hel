package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.request;

import lombok.Data;

@Data
public class QueryTransactionByTransactionUuidRequest {

    private String transactionUUID;
}
