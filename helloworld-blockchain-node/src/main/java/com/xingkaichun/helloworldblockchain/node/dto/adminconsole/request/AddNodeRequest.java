package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import lombok.Data;

@Data
public class AddNodeRequest {

    private String ip;
    private int port;
}
