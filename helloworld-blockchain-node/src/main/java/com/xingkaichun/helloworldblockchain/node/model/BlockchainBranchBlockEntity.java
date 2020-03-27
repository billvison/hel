package com.xingkaichun.helloworldblockchain.node.model;


import lombok.Data;

import java.math.BigInteger;

@Data
public class BlockchainBranchBlockEntity {

    private BigInteger blockHeight;
    private String blockHash;
}
