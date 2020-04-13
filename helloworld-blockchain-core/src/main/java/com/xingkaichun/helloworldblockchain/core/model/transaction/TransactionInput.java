package com.xingkaichun.helloworldblockchain.core.model.transaction;

import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import lombok.Data;

import java.io.Serializable;

/**
 * 交易输入
 */
@Data
public class TransactionInput implements Serializable {

    //交易的输入是一笔交易的输出
    private TransactionOutput unspendTransactionOutput;
    //TODO 完善对象信息
    //交易输入对应的公钥
    private StringPublicKey stringPublicKey;

    //交易签名 签名应当能代表一个交易
    private String signature;
}