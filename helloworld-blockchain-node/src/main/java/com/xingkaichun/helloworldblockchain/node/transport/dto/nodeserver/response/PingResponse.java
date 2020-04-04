package com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.Node;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class PingResponse {

    private BigInteger blockChainHeight ;
    private List<Node> nodeList;
}
