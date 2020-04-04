package com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.request;

import lombok.Data;

import java.math.BigInteger;

@Data
public class RemoveBlockRequest {

    /**
     * 删除区块的高度。因为区块是连续的，所以大于等于这个高度的区块都将被删除
     */
    private BigInteger blockHeight;
}
