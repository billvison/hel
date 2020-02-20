package com.xingkaichun.blockchain.core.dto;


import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 交易
 */
@Data
public class TransactionDTO implements Serializable {

    //交易时间戳
    private long timestamp;
    //交易ID
    private String transactionUUID;
    //交易类型
    private TransactionType transactionType;
    //交易输入
    private ArrayList<String> inputs;
    //交易输出
    private ArrayList<TransactionOutputDTO> outputs;
    //交易签名
    private String signature;
}
