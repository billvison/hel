package com.xingkaichun.helloworldblockchain.model.transaction;

import com.xingkaichun.helloworldblockchain.model.key.StringPublicKey;
import lombok.Data;

import java.io.Serializable;

/**
 * 交易输入
 */
@Data
public class TransactionInput implements Serializable {

    //交易的输入是一笔交易的输出
    private TransactionOutput unspendTransactionOutput;
    //交易输入对应的公钥
    private StringPublicKey stringPublicKey;
}