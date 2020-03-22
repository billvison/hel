package com.xingkaichun.helloworldblockchain.node.model;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import lombok.Data;

@Data
public class NodeEntity extends SimpleNode{

    private int blockChainHeight;
    private boolean isNodeAvailable;
    private int errorConnectionTimes;
    private boolean fork;
}
