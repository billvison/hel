package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class AddOrUpdateNodeRequest {

    private int port;
    private BigInteger blockChainHeight;




    //region get set

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public BigInteger getBlockChainHeight() {
        return blockChainHeight;
    }

    public void setBlockChainHeight(BigInteger blockChainHeight) {
        this.blockChainHeight = blockChainHeight;
    }

    //endregion
}
