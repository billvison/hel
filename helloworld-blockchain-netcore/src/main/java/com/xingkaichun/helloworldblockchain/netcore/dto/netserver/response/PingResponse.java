package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

import java.util.List;

/**
 *
 * @author 邢开春
 */
public class PingResponse {

    private Long blockchainHeight ;
    private List<NodeDto> nodeList;




    //region get set

    public Long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

    public List<NodeDto> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<NodeDto> nodeList) {
        this.nodeList = nodeList;
    }

    //endregion
}
