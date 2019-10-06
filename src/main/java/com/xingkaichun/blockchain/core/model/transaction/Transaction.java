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

    //交易类型
    private TransactionType transactionType;
    //交易输入
    private ArrayList<TransactionInput> inputs;
    //交易输出
    private ArrayList<TransactionOutput> outputs;


    /**
     * 创建交易
     */
    public Transaction(TransactionType transactionType, ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        this.transactionUUID = String.valueOf(UUID.randomUUID());
        this.transactionType =transactionType;
        this.inputs = inputs;
        this.outputs = outputs;
    }

}
