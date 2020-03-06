package com.xingkaichun.helloworldblockchain.node.dto.node.response;

import com.xingkaichun.helloworldblockchain.node.dto.node.Node;
import lombok.Data;

import java.util.List;

@Data
public class GetNodesResponse {

    private List<Node> nodeList ;
}
