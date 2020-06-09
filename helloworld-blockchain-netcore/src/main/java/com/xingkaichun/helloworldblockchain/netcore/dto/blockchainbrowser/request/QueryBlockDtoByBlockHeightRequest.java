package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.request;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryBlockDtoByBlockHeightRequest {

    private BigInteger blockHeight;




    //region get set

    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(BigInteger blockHeight) {
        this.blockHeight = blockHeight;
    }

    //endregion
}
