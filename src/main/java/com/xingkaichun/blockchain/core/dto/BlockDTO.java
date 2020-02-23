package com.xingkaichun.blockchain.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class BlockDTO {
    private long timestamp;
    private String previousHash;
    private Integer height;
    private List<TransactionDTO> transactions;
    private String merkleRoot;
    private Long nonce;
    private String hash;
}
