package com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QueryBlockHashByBlockHeightRequest {

    private BigInteger blockHeight;

}
