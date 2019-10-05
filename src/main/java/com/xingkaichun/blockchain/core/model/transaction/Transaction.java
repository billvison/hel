package com.xingkaichun.blockchain.core.model.transaction;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 交易
 */
@Data
public class Transaction implements Serializable {

    //交易ID
    private String transactionUUID;
    //交易签名
    private byte[] signature;

    //交易输入
    //约定:每笔交易输入不可为空
    private ArrayList<TransactionInput> inputs;
    //交易输出
    private ArrayList<TransactionOutput> outputs;

    /**
     * 创建交易
     */
    public Transaction(ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        this.transactionUUID = String.valueOf(UUID.randomUUID());
        this.inputs = inputs;
        this.outputs = outputs;
    }

}
