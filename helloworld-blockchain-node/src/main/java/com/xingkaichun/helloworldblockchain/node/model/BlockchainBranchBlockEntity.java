package com.xingkaichun.helloworldblockchain.node.model;


import lombok.Data;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class BlockchainBranchBlockEntity {

    private BigInteger blockHeight;
    private String blockHash;
}
