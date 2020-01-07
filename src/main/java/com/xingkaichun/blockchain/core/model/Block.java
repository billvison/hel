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

    //TODO 校验 时间戳:为防止作恶。区块的时间戳一定比前一个区块的时间戳大，一定比当前时间小。
    private long timestamp;
    //上一个区块的哈希
    private String previousHash;
    //区块高度
    private Integer height;
    //区块随机数
    private Long nonce;
    //默克尔树根
    private String merkleRoot;
    //区块哈希
    private String hash;
    //区块里的交易
    private List<Transaction> transactions;

}
