package com.xingkaichun.helloworldblockchain.node.transport.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易输入
 */
@Data
public class TransactionInputDTO implements Serializable {

    //交易的输入
    private String unspendTransactionOutputUUID;
    //脚本钥匙
    private List<String> scriptKey;
}