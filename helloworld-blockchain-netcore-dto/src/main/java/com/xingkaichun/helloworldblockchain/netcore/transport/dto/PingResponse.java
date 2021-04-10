package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class PingResponse {

    private Long blockchainHeight;
    private List<NodeDTO> nodes;




    //region get set

    public Long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

    public List<NodeDTO> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDTO> nodes) {
        this.nodes = nodes;
    }

//endregion
}
