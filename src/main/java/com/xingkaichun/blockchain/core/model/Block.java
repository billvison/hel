package com.xingkaichun.blockchain.core.model;


import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.MerkleUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区块
 */
@Data
public class Block implements Serializable {

    //区块高度
    private int blockHeight;
    //区块随机数
    private int nonce;
    //区块哈希
    private String hash;
    //上一个区块哈希
    private String previousHash;
    //区块里的交易
    private List<Transaction> transactions;
    //默克尔树根
    private String merkleRoot;

}
