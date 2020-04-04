package com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.Node;
import lombok.Data;

@Data
public class AddNodeRequest {

    private Node node;
}
