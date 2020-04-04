package com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.Node;
import lombok.Data;

import java.util.List;

@Data
public class QueryNodeListResponse {

    private List<Node> nodeList;
}
