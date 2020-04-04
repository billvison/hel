package com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AddOrUpdateNodeRequest {

    private int port;
    private BigInteger blockChainHeight;
}
