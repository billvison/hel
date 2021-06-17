package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

/**
 * 虚拟机
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class VirtualMachine {

    /**
     * 执行脚本
     */
    public abstract ScriptExecuteResult executeScript(Transaction transactionEnvironment, Script script) throws RuntimeException ;

    /**
     * 检验交易脚本，即校验交易输入能解锁交易输出吗？即用户花费的是自己的钱吗？
     * 校验用户花费的是自己的钱吗，用户只可以花费自己的钱。专业点的说法，校验UTXO所有权，用户只可以花费自己拥有的UTXO。
     * 用户如何能证明自己拥有这个UTXO，只要用户能创建出一个能解锁(该UTXO对应的交易输出脚本)的交易输入脚本，就证明了用户拥有该UTXO。
     * 这是因为锁(交易输出脚本)是用户创建的，自然只有该用户有对应的钥匙(交易输入脚本)，自然意味着有钥匙的用户拥有这把锁的所有权。
     */
    public abstract boolean checkTransactionScript(Transaction transaction);

}
