package com.xingkaichun.helloworldblockchain.node.dto.nodeserver;

import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class SimpleNode {

    private String ip;
    private int port;

    public SimpleNode() {
    }

    public SimpleNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
