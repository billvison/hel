package com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch;


import lombok.Data;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class BlockchainBranchBlockDto {

    private BigInteger blockHeight;
    private String blockHash;
}
