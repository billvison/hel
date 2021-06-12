package com.xingkaichun.helloworldblockchain.core.model.transaction;

import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;

import java.io.Serializable;

/**
 * 交易输入：交易的付款方叫作交易输入。
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionInput implements Serializable {

    /**
     * 交易的输入是一笔交易的输出
     * 在此笔交易(此交易输入所在的交易)发生之前，交易的输入是一个未花费交易输出。
     */
    private TransactionOutput unspentTransactionOutput;
    /**
     * [输入脚本]
     * [输入脚本]用于解锁交易输出的[输出脚本]。解锁成功，则证明了(持有[输入脚本]的)用户拥有([输出脚本]所在的)交易输出的所有权，
     * 拥有交易输出所有权的用户才被允许使用这个交易输出。
     *
     * 请结合[输出脚本]理解[输入脚本]
     * @see com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput
     *
     * 这里特别注意
     * 若交易广播到区块链网络，任何用户都有可能获取交易，并从交易中获取输入脚本。
     * 如果输入脚本不够安全，恶意用户[用获得的输入脚本]构造自己的交易，然后比你快一步将自己的交易放入下一个区块，
     * 区块链网络节点都认同恶意用户的交易，从而花费了你的钱。
     *
     * 如果输入脚本不够安全，恶意用户获得的输入脚本构造自己的交易，你的交易放入分叉A，恶意用户的交易放入分叉B，
     * 若B的链的长度大于A，区块链网络节点都认同恶意用户的交易，从而花费了你的钱。
     */
    private InputScript inputScript;




    //region get set


    public TransactionOutput getUnspentTransactionOutput() {
        return unspentTransactionOutput;
    }

    public void setUnspentTransactionOutput(TransactionOutput unspentTransactionOutput) {
        this.unspentTransactionOutput = unspentTransactionOutput;
    }

    public InputScript getInputScript() {
        return inputScript;
    }

    public void setInputScript(InputScript inputScript) {
        this.inputScript = inputScript;
    }

    //endregion
}