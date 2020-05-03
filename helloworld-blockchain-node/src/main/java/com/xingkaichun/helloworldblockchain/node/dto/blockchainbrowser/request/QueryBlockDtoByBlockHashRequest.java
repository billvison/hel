package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryBlockDtoByBlockHashRequest {

    private String blockHash;




    //region get set

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    //endregion
}
