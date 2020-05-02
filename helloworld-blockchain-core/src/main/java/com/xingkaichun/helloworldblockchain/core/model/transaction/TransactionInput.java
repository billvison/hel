package com.xingkaichun.helloworldblockchain.core.model.transaction;

import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import lombok.Data;

import java.io.Serializable;

/**
 * 交易输入
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class TransactionInput implements Serializable {

    //交易的输入是一笔交易的输出
    private TransactionOutput unspendTransactionOutput;
    //脚本钥匙
    private ScriptKey scriptKey;
}