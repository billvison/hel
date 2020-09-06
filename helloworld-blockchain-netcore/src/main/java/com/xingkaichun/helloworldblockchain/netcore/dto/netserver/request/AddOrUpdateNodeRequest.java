package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class AddOrUpdateNodeRequest {

    private int port;
    private Long blockChainHeight;




    //region get set

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Long getBlockChainHeight() {
        return blockChainHeight;
    }

    public void setBlockChainHeight(Long blockChainHeight) {
        this.blockChainHeight = blockChainHeight;
    }

//endregion
}
