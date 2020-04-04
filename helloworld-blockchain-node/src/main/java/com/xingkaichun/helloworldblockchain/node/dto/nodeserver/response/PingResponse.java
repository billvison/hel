package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class PingResponse {

    private BigInteger blockChainHeight ;
    private List<Node> nodeList;
}
