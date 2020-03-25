package com.xingkaichun.helloworldblockchain.node.model;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import lombok.Data;

@Data
public class NodeEntity extends SimpleNode{

    private Integer blockChainHeight;
    private Boolean isNodeAvailable;
    private Integer errorConnectionTimes;
    private Boolean fork;
}
