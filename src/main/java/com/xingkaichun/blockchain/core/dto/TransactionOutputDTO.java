package com.xingkaichun.blockchain.core.dto;


import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易输出
 */
@Data
public class TransactionOutputDTO implements Serializable {

    //交易输出的ID
    private String transactionOutputUUID;
    //交易输出的接收方 TODO 地址
    private PublicKeyString reciepient;
    //交易输出的金额
    private BigDecimal value;

}
