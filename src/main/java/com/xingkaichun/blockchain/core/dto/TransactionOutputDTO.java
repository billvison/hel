package com.xingkaichun.blockchain.core.dto;


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
    //交易输出的地址
    private String address;
    //交易输出的金额
    private BigDecimal value;

}
