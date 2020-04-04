package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import lombok.Data;

@Data
public class QueryNodeListRequest {

    private SimpleNode node;
}
