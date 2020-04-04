package com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.SimpleNode;
import lombok.Data;

@Data
public class DeleteNodeRequest {

    private SimpleNode node;
}
