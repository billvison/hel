package com.xingkaichun.helloworldblockchain.node.dto.nodeserver;

import lombok.Data;

@Data
public class Node extends SimpleNode{

    private int blockChainHeight;
    private boolean isNodeAvailable;
    private int errorConnectionTimes;
}
