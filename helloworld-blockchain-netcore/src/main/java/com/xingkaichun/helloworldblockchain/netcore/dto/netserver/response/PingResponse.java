package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class PingResponse {

    private String blockChainId;
    private Long blockChainVersion;
    private BigInteger blockChainHeight ;
    private List<NodeDto> nodeList;




    //region get set

    public String getBlockChainId() {
        return blockChainId;
    }

    public void setBlockChainId(String blockChainId) {
        this.blockChainId = blockChainId;
    }

    public Long getBlockChainVersion() {
        return blockChainVersion;
    }

    public void setBlockChainVersion(Long blockChainVersion) {
        this.blockChainVersion = blockChainVersion;
    }

    public BigInteger getBlockChainHeight() {
        return blockChainHeight;
    }

    public void setBlockChainHeight(BigInteger blockChainHeight) {
        this.blockChainHeight = blockChainHeight;
    }

    public List<NodeDto> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<NodeDto> nodeList) {
        this.nodeList = nodeList;
    }

    //endregion
}
