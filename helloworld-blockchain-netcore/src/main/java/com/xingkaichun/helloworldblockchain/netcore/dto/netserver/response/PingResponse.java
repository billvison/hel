package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDTO;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class PingResponse {

    private Long blockchainHeight ;
    private List<NodeDTO> nodeList;




    //region get set

    public Long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

    public List<NodeDTO> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<NodeDTO> nodeList) {
        this.nodeList = nodeList;
    }

    //endregion
}
