package com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.Node;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class PingResponse {

    private String blockChainId;
    private Long blockChainVersion;
    private BigInteger blockChainHeight ;
    private List<Node> nodeList;




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

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    //endregion
}
