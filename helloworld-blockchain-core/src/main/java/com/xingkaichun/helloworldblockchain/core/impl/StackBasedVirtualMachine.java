package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.VirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.*;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.tools.ScriptTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.List;

/**
 * 基于栈的虚拟机
 *
 * @author 邢开春 409060350@qq.com
 */
public class StackBasedVirtualMachine extends VirtualMachine {

    @Override
    public ScriptExecuteResult executeScript(Transaction transactionEnvironment, Script script) throws RuntimeException {
        ScriptExecuteResult stack = new ScriptExecuteResult();

        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(ByteUtil.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode)){
                if(stack.size()<1){
                    throw new RuntimeException("指令运行异常");
                }
                stack.push(stack.peek());
            }else if(ByteUtil.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode)){
                if(stack.size()<1){
                    throw new RuntimeException("指令运行异常");
                }
                String publicKey = stack.pop();
                String publicKeyHash = AccountUtil.publicKeyHashFromPublicKey(publicKey);
                stack.push(publicKeyHash);
            }else if(ByteUtil.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode)){
                if(stack.size()<2){
                    throw new RuntimeException("指令运行异常");
                }
                if(!StringUtil.isEquals(stack.pop(),stack.pop())){
                    throw new RuntimeException("脚本执行失败");
                }
            }else if(ByteUtil.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                if(stack.size()<2){
                    throw new RuntimeException("指令运行异常");
                }
                String publicKey = stack.pop();
                String signature = stack.pop();
                String message = TransactionTool.signatureHashAll(transactionEnvironment);
                boolean verifySignatureSuccess = AccountUtil.verifySignature(publicKey,message,signature);
                if(!verifySignatureSuccess){
                    throw new RuntimeException("脚本执行失败");
                }
                stack.push(HexUtil.bytesToHexString(BooleanEnum.TRUE.getCode()));
            }else if(ByteUtil.equals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                if(script.size()<i+2){
                    throw new RuntimeException("指令运行异常");
                }
                ++i;
                stack.push(script.get(i));
            }else {
                throw new RuntimeException("不能识别的操作码");
            }
        }
        return stack;
    }

    public boolean checkTransactionScript(Transaction transaction) {
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    //锁(交易输出脚本)
                    OutputScript outputScript = transactionInput.getUnspentTransactionOutput().getOutputScript();
                    //钥匙(交易输入脚本)
                    InputScript inputScript = transactionInput.getInputScript();
                    //完整脚本
                    Script script = ScriptTool.createScript(inputScript,outputScript);
                    //执行脚本
                    ScriptExecuteResult scriptExecuteResult = executeScript(transaction,script);
                    //脚本执行结果是个栈，如果栈有且只有一个元素，且这个元素是0x01，则解锁成功。
                    boolean executeSuccess = scriptExecuteResult.size()==1 && ByteUtil.equals(BooleanEnum.TRUE.getCode(),HexUtil.hexStringToBytes(scriptExecuteResult.pop()));
                    if(!executeSuccess){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            LogUtil.error("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。",e);
            return false;
        }
        return true;
    }
}
