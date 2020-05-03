package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryBlockHashByBlockHeightRequest {

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
