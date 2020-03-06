package com.xingkaichun.helloworldblockchain.node.dto.node;

import lombok.Data;

@Data
public class Node {

    private String ip;
    private int port;
    private int blockChainHeight;
    private boolean isNodeAvailable;
    private int errorConnectionTimes;
}
