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

    /**
     * TODO 校验 时间戳:为防止作恶。区块的时间戳一定比前一个区块的时间戳大，一定比当前时间小。
     * 时间戳产生时间
     * 为什么需要时间戳这个字段？
     */
    private long timestamp;
    //上一个区块的哈希
    private String previousHash;
    //区块高度
    private Integer height;
    //区块随机数
    private Long nonce;
    /**
     * 默克尔树根
     * 由transactions生成
     * 既然这个字段是有由交易列表生成的，这个字段每次需要时完全可以自己生成？为什么需要这个字段？请参考SPV。
     */
    private String merkleRoot;
    /**
     * 区块哈希：由timestamp、previousHash、height、nonce、merkleRoot共同作用生成。
     * 为什么需要时间戳这个字段？
     */
    private String hash;
    //区块里的交易
    private List<Transaction> transactions;

}
