package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QueryBlockDtoByBlockHashRequest {

    private String blockHash;
}
