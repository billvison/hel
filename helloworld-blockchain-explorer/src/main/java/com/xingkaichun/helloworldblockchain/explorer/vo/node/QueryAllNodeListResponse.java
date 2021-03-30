package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
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
