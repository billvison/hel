package com.xingkaichun.helloworldblockchain.core.script;

import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.TransactionUtil;
import com.xingkaichun.helloworldblockchain.crypto.KeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;

public class ScriptMachine {


    public static final String OP_PREFIX = "OP_";
    public static final String DATA_PREFIX = "DATA_";

    public static final String OP_DUP = OP_PREFIX + "DUP";
    public static final String OP_HASH160 = OP_PREFIX + "HASH160";
    public static final String OP_EQUALVERIFY = OP_PREFIX + "EQUALVERIFY";
    public static final String OP_CHECKSIG = OP_PREFIX + "CHECKSIG";

    public ScriptExecuteResult executeScript(Transaction transaction, Script script) throws Exception {
        ScriptExecuteResult stack = new ScriptExecuteResult();
        for(int i=0;i<script.size();i++){
            String command = script.get(i);
            //数据
            if(command.startsWith(DATA_PREFIX)){
                stack.push(getData(command));
            }else if(command.startsWith(OP_PREFIX)){
                if(OP_DUP.equals(command)){
                    stack.push(stack.peek());//复制栈顶元素放入栈
                }else if(OP_HASH160.equals(command)){
                    //对栈顶元素求地址，并将结果放入栈顶
                    String top = stack.pop();//TODO 公钥hash 不是地址
                    String hash160 = KeyUtil.stringAddressFrom(new StringPublicKey(top)).getValue();
                    stack.push(hash160);
                }else if(OP_EQUALVERIFY.equals(command)){
                    //比较栈顶的前两个元素是否相等，不等则报错，相等，则移除这两个元素
                    if(!stack.pop().equals(stack.pop())){
                        throw new RuntimeException("脚本执行失败");
                    }
                }else if(OP_CHECKSIG.equals(command)){
                    String publicKey = stack.pop();
                    String sig = stack.pop();
                    KeyUtil.verifySignature(new StringPublicKey(publicKey), TransactionUtil.signatureData(transaction),sig);
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

    public static String getData(String dataCommand){
        return dataCommand.substring(DATA_PREFIX.length());
    }
    public static String getDataCommand(String data){
        return DATA_PREFIX + data;
    }

/*    public static Script createPayToClassicAddressScript(String sign,String publicKey,String address) {
        Script script = new Script();
        script.add(getDataCommand(sign));//将签名入栈
        script.add(getDataCommand(publicKey));//将公钥入栈
        script.add(OP_DUP);//复制公钥
        script.add(OP_HASH160);//hash
        script.add(getDataCommand(address));//地址入栈
        script.add(OP_EQUALVERIFY);
        script.add(OP_CHECKSIG);
        return script;
    }*/
    public static Script createPayToClassicAddressScript(ScriptKey scriptKey, ScriptLock scriptLock) {
        Script script = new Script();
        script.addAll(scriptKey);
        script.addAll(scriptLock);
        return script;
    }
    public static ScriptKey createPayToClassicAddressInputScript(String sign,String publicKey) {
        ScriptKey script = new ScriptKey();
        script.add(getDataCommand(sign));//将签名入栈
        script.add(getDataCommand(publicKey));//将公钥入栈
        return script;
    }

    public static ScriptLock createPayToClassicAddressOutputScript(String address) {
        ScriptLock script = new ScriptLock();
        script.add(OP_DUP);//复制公钥
        script.add(OP_HASH160);//hash
        script.add(getDataCommand(address));//地址入栈
        script.add(OP_EQUALVERIFY);
        script.add(OP_CHECKSIG);
        return script;
    }
}
