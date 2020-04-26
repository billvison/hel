package com.xingkaichun.helloworldblockchain.node.transport.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * 区块
 */
@Data
public class BlockDTO implements Serializable {
    private long timestamp;
    private BigInteger height;
    private List<TransactionDTO> transactions;
    private BigInteger nonce;
}
