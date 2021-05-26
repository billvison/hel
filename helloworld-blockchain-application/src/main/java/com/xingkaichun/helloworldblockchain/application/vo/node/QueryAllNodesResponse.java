package com.xingkaichun.helloworldblockchain.application.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.model.Node;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryAllNodesResponse {

    private List<Node> nodes;




    //region get set
    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
    //endregion
}
