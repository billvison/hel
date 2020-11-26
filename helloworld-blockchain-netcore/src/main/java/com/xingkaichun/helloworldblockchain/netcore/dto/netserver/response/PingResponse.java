package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class PingResponse {

    private Long blockchainVersion;
    private Long blockchainHeight ;
    private List<NodeDto> nodeList;




    //region get set


    public Long getBlockchainVersion() {
        return blockchainVersion;
    }

    public void setBlockchainVersion(Long blockchainVersion) {
        this.blockchainVersion = blockchainVersion;
    }

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
