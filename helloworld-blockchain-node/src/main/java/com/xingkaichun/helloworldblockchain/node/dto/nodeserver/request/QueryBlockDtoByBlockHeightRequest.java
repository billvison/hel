package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QueryBlockDtoByBlockHeightRequest {

    private BigInteger blockHeight;
}
