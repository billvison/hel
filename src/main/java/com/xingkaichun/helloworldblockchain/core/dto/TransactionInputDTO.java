package com.xingkaichun.helloworldblockchain.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 交易输入
 */
@Data
public class TransactionInputDTO implements Serializable {

    //交易的输入
    private String unspendTransactionOutputUUID;
    //交易的输入对应的公钥
    private String publicKey;
}