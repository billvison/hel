package com.xingkaichun.blockchain.core.model.transaction;


import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 交易输出
 */
@Data
public class TransactionOutput implements Serializable {

    //交易的ID
    private String transactionId;
    //交易输出的ID
    private String transactionOutputUUID;
    //交易输出的接收方
    private PublicKeyString reciepient;
    //交易输出的金额
    private BigDecimal value;


    public TransactionOutput(PublicKeyString reciepient, BigDecimal value, String transactionId) {
        this.transactionId = transactionId;
        this.transactionOutputUUID = String.valueOf(UUID.randomUUID());
        this.reciepient = reciepient;
        this.value = value;
    }

}
