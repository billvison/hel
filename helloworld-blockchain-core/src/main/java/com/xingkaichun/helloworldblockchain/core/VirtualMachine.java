package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

/**
 * 虚拟机
 * 用于执行数字货币脚本。
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class VirtualMachine {

    /**
     * 执行脚本
     */
    public abstract ScriptExecuteResult executeScript(Transaction transactionEnvironment, Script script) throws RuntimeException ;

}
