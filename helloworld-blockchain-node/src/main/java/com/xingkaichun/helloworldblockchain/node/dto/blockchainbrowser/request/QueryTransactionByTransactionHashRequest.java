package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

@Data
public class QueryTransactionByTransactionHashRequest {

    private String transactionHash;
}
