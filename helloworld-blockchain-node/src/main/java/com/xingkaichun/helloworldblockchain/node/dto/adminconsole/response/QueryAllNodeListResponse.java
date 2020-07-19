package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryAllNodeListResponse {

    private List<NodeDto> nodeList;




    //region get set

    public List<NodeDto> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<NodeDto> nodeList) {
        this.nodeList = nodeList;
    }


    //endregion
}
