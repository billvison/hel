package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import lombok.Data;

@Data
public class UpdateNodeRequest {

    private Node node;
}
