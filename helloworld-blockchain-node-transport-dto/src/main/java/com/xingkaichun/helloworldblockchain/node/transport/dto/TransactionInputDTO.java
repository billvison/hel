package com.xingkaichun.helloworldblockchain.node.transport.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易输入
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class TransactionInputDTO implements Serializable {

    //交易的输入
    private String unspendTransactionOutputHash;
    //脚本钥匙
    private List<String> scriptKey;
}