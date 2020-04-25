package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

@Data
public class QueryMiningTransactionByTransactionHashRequest {

    private String transactionHash;
}
