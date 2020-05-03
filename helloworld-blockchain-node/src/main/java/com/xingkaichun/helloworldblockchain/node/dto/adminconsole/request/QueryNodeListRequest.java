package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryNodeListRequest {

    private SimpleNode node;




    //region get set
    public SimpleNode getNode() {
        return node;
    }

    public void setNode(SimpleNode node) {
        this.node = node;
    }
    //endregion
}
