package com.xingkaichun.helloworldblockchain.node.dto.nodeserver;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Node extends SimpleNode{

    private BigInteger blockChainHeight;
    private Boolean isNodeAvailable;
    private Integer errorConnectionTimes;
    private Boolean fork;
}
