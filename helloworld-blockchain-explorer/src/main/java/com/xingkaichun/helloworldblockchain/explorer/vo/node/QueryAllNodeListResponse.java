package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDTO;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryAllNodeListResponse {

    private List<NodeDTO> nodeList;




    //region get set

    public List<NodeDTO> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<NodeDTO> nodeList) {
        this.nodeList = nodeList;
    }


    //endregion
}
