package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request;

/**
 *
 * @author 邢开春
 */
public class AddOrUpdateNodeRequest {

    private Long blockchainHeight;




    //region get set

    public Long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

//endregion
}
