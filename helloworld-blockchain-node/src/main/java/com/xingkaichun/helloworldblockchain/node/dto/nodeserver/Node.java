package com.xingkaichun.helloworldblockchain.node.dto.nodeserver;

import lombok.Data;

@Data
public class Node extends SimpleNode{

    private Integer blockChainHeight;
    private Boolean isNodeAvailable;
    private Integer errorConnectionTimes;
    private Boolean fork;
}
