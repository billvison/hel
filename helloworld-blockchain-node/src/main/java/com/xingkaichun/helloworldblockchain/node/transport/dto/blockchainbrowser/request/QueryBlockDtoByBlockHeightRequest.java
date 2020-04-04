package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QueryBlockDtoByBlockHeightRequest {

    private BigInteger blockHeight;
}
