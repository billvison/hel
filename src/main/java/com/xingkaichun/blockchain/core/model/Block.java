package com.xingkaichun.blockchain.core.model;


import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区块
 */
@Data
public class Block implements Serializable {
    //区块高度 区块id
    private int blockHeight;
    //区块随机数
    private int nonce;
    //区块哈希
    private String hash;
    //上一个区块哈希
    private String previousHash;
    //区块里的交易
    private List<Transaction> transactions;
    //默克尔树
    private String merkleRoot;

    public Block(int blockHeight, String previousHash, List<Transaction> transactions, String merkleRoot) {
        this.blockHeight = blockHeight;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.merkleRoot = merkleRoot;
    }

}
