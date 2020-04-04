package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import lombok.Data;

import java.util.List;

@Data
public class QueryNodeListResponse {

    private List<Node> nodeList;
}
