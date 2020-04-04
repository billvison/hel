package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QueryBlockDtoByBlockHeightRequest {

    private BigInteger blockHeight;
}
