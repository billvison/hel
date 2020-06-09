package com.xingkaichun.helloworldblockchain.netcore.model;


import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainBranchBlockEntity {

    private BigInteger blockHeight;
    private String blockHash;




    //region get set

    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(BigInteger blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    //endregion
}
