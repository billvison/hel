package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AddOrUpdateNodeRequest {

    private BigInteger blockChainHeight;
}
