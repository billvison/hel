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
     * 区块产生的时间戳
     * 为防止作恶。区块的时间戳一定比前一个区块的时间戳大，一定比当前时间小，一定大于0。
     * 为什么需要时间戳这个字段？
     * 这个时间戳很有用处，当然没人规定区块链一定要有这个字段，拿掉它也是可以的。以下列举一下这个字段可能的用处:
     * 记录区块形成的时间。
     * 记录了区块的时间，可以利用这个值用于估算每次产生区块所需的共识的时间，动态调整共识难度。
     */
    private long timestamp;
    //上一个区块的哈希
    private String previousHash;
    //区块高度
    private Integer height;
    //区块随机数
    //这个随机数用于共识。
    private Long nonce;
    /**
     * 默克尔树根
     * 由transactions生成
     * 既然这个字段是有由交易列表生成的，这个字段每次需要时完全可以自己生成？为什么需要这个字段？请参考SPV。
     */
    private String merkleRoot;
    /**
     * 区块哈希：由timestamp、previousHash、height、nonce、merkleRoot共同作用生成。
     * 为什么需要时间戳这个字段？区块哈希可以用于确认整个区块的数据内容。
     * 区块哈希确定了，反过来说其它字段的值也是确定的。TODO
     */
    private String hash;
    //区块里的交易
    private List<Transaction> transactions;
}
