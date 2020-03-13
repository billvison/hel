package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import lombok.Data;

import java.util.List;

@Data
public class PingResponse {

    private int blockChainHeight ;
    private List<Node> nodeList;
}
