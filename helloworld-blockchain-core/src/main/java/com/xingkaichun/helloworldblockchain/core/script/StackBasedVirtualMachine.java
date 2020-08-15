package com.xingkaichun.helloworldblockchain.core.script;

import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;

/**
 * 基于栈的虚拟机
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class StackBasedVirtualMachine {

    /**
     * 执行脚本
     */
    public ScriptExecuteResult executeScript(Transaction transactionEnvironment, Script script) throws RuntimeException {
        ScriptExecuteResult stack = new ScriptExecuteResult();
        for(int i=0;i<script.size();i++){
            String command = script.get(i);
            //数据
            if(command.startsWith(ScriptOperationCodes.OPERATION_DATA_PREFIX)){
                stack.push(ScriptOperationCodes.getDataFromOperationData(command));
            }else if(command.startsWith(ScriptOperationCodes.OPERATION_CODE_PREFIX)){
                if(ScriptOperationCodes.OP_DUP.equals(command)){
                    stack.push(stack.peek());
                }else if(ScriptOperationCodes.OP_HASH160.equals(command)){
                    String top = stack.peek();
                    String address = AccountUtil.addressFromPublicKey(top);
                    stack.pop();
                    stack.push(address);
                }else if(ScriptOperationCodes.OP_EQUALVERIFY.equals(command)){
                    if(!stack.pop().equals(stack.pop())){
                        throw new RuntimeException("脚本执行失败");
                    }
                }else if(ScriptOperationCodes.OP_CHECKSIG.equals(command)){
                    String publicKey = stack.pop();
                    String sign = stack.pop();
                    boolean verifySignatureSuccess = AccountUtil.verifySignature(publicKey, TransactionTool.getSignatureData(transactionEnvironment),sign);
                    if(!verifySignatureSuccess){
                        throw new RuntimeException("脚本执行失败");
                    }
                    stack.push(String.valueOf(Boolean.TRUE));
                }else {
                    throw new RuntimeException("没有指令");
                }
            }else {
                throw new RuntimeException("指令错误");
            }
        }
        return stack;
    }

    public static Script createPayToClassicAddressScript(ScriptKey scriptKey, ScriptLock scriptLock) {
        Script script = new Script();
        script.addAll(scriptKey);
        script.addAll(scriptLock);
        return script;
    }

    public static ScriptKey createPayToClassicAddressInputScript(String sign,String publicKey) {
        ScriptKey script = new ScriptKey();
        script.add(ScriptOperationCodes.getOperationDataFromData(sign));
        script.add(ScriptOperationCodes.getOperationDataFromData(publicKey));
        return script;
    }

    public static ScriptLock createPayToClassicAddressOutputScript(String address) {
        ScriptLock script = new ScriptLock();
        script.add(ScriptOperationCodes.OP_DUP);
        script.add(ScriptOperationCodes.OP_HASH160);
        script.add(ScriptOperationCodes.getOperationDataFromData(address));
        script.add(ScriptOperationCodes.OP_EQUALVERIFY);
        script.add(ScriptOperationCodes.OP_CHECKSIG);
        return script;
    }
}
