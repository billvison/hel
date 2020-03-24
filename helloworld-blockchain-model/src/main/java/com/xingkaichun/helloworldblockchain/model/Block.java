package com.xingkaichun.helloworldblockchain.model;


import com.xingkaichun.helloworldblockchain.model.transaction.Transaction;
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
     * 区块不允许放入超过一定时间产生的交易(例如一年前...)。
     */
    private long timestamp;
    /**
     * 上一个区块的哈希
     */
    private String previousHash;
    /**
     * 区块高度
     * 冗余字段，这个值可以由区块链计算出来
     */
    private Integer height;
    //区块里的交易
    private List<Transaction> transactions;
    /**
     * 默克尔树根
     * 由transactions生成。
     * 既然这个字段是有由交易列表生成的，这个字段每次需要时完全可以自己生成？为什么需要这个字段？请参考SPV。
     */
    private String merkleRoot;
    //区块随机数
    //这个随机数用于共识。
    private Long nonce;
    /**
     * 区块哈希：由timestamp、previousHash、height、nonce、merkleRoot共同作用使用Hash算法生成。
     * 为什么需要哈希这个字段？
     * 区块哈希确定了，反过来说其它字段的值也是确定的，也就是说区块的数据是确定的。
     * 我们知道区块链的区块需要指向上一个区块？怎么指向？
     * 我们就在区块里加上previousHash这个字段，这样上一个区块哈希确定了，自然上一个区块的数据也就确定了。
     * 自然上上一个区块的数据也就确定了，自然所有区块的数据也就确定了。
     * 可以想象，如果我们得到一个区块链的最后一个区块的哈希，那一个区块链的所有数据都是确定下来了，谁也不能伪造。
     *
     * 如果不对区块产生的hash做约束的话，同一区块链上不同的位置区块可能产生相同的hash，
     * 为了简单，请保证同一区块链上不出现区块hash相同的两个区块。
     *
     * 区块哈希，由矿工产生，区块链系统进行唯一性校验、格式校验。
     * 这个值的构成应当足够简单去验证这个值是否是唯一的。
     * 当区块数据足够庞大时，用户节点只有最近一部分区块与UTXO数据，这时节点必须也可以校验它的唯一性。
     * 这里建议它的构成是一串字符+时间戳。
     * 因为后产生的区块时间戳更大，因此只要校验这个时间戳是否比上一个区块产生的时间戳更大，就可校验它的唯一性。
     */
    private String hash;

    /**
     * 挖矿难度。
     * 这个字段是冗余的。可以通过区块链系统计算出来。
     */
    private ConsensusTarget consensusTarget;
}
