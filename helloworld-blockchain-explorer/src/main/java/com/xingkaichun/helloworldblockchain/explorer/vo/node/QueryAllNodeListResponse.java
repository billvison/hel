package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryAllNodeListResponse {

    private List<NodeEntity> nodeList;




    //region get set

    public List<NodeEntity> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<NodeEntity> nodeList) {
        this.nodeList = nodeList;
    }


    //endregion
}
