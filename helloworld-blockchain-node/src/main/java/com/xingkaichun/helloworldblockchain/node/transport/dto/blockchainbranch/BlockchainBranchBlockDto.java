package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbranch;


import lombok.Data;

import java.math.BigInteger;

@Data
public class BlockchainBranchBlockDto {

    private BigInteger blockHeight;
    private String blockHash;
}
