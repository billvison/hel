package com.xingkaichun.blockchain.core.model.transaction;


import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 交易
 */
@Data
public class Transaction implements Serializable {

    //交易时间戳
    private long timestamp;
    //交易ID
    private String transactionUUID;
    //交易类型
    private TransactionType transactionType;
    //花钱人公钥 TODO 冗余了？
    private PublicKeyString sender;
    //交易输入
    //TODO 校验金额 或是  List<String> utxoUuids? 注意反序列化时，将这个值补充完整
    private ArrayList<TransactionInput> inputs;
    //交易输出
    private ArrayList<TransactionOutput> outputs;
    //交易签名
    private String signature;
}
