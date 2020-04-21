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
    //TODO 持续检查缩减dto字段
    private long timestamp;
    private BigInteger height;
    private String previousHash;
    private List<TransactionDTO> transactions;
    private BigInteger nonce;

    //TODO 移除
    private String hash;
}
