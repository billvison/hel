package com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch;


import lombok.Data;

import java.math.BigInteger;

@Data
public class BlockchainBranchBlockDto {

    private BigInteger blockHeight;
    private String blockHash;
}
