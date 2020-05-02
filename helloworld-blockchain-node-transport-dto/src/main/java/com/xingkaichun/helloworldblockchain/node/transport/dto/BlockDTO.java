package com.xingkaichun.helloworldblockchain.node.transport.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * 区块
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class BlockDTO implements Serializable {
    private long timestamp;
    private BigInteger height;
    private List<TransactionDTO> transactions;
    private String consensusValue;
}
