package com.xingkaichun.helloworldblockchain.core.script;

import com.xingkaichun.helloworldblockchain.core.exception.ExecuteScriptException;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPublicKey;

/**
 * 脚本机器
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ScriptMachine {

    //操作码前缀
    public static final String OPERATION_CODE_PREFIX = "0";
    //操作数前缀
    public static final String OPERATION_DATA_PREFIX = "1";

    /**
     * 将栈顶的元素复制一份，并放入栈。
     */
    public static final String OPERATION_CODE_DUPLICATE = OPERATION_CODE_PREFIX + "1";
    /**
     * 把栈顶元素当做公钥，求地址。然后删除栈顶元素，然后将地址放入栈。
     */
    public static final String OPERATION_CODE_PUBLIC_KEY_TO_CLASSIC_ADDRESS = OPERATION_CODE_PREFIX + "2";
    /**
     * 比较栈顶的前两个元素是否相等，不等抛出异常。
     * 无论是否相等，最后从栈顶移除这两个元素。
     */
    public static final String OPERATION_CODE_EQUAL_VERIFY = OPERATION_CODE_PREFIX + "3";
    /**
     * 栈顶第一个元素是公钥
     * 栈顶第二个元素是交易签名
     * 通过公钥校验交易签名是否正确。
     * 如果校验成功，先从栈中移除这两个元素，然后将true放入栈。
     * 如果校验失败，抛出异常。
     * 无论如何，栈中一定要移除这两个元素。
     */
    public static final String OPERATION_CODE_CHECK_SIGN = OPERATION_CODE_PREFIX + "4";

    /**
     * 执行脚本
     */
    public ScriptExecuteResult executeScript(Transaction transactionEnvironment, Script script) throws ExecuteScriptException {
        ScriptExecuteResult stack = new ScriptExecuteResult();
        for(int i=0;i<script.size();i++){
            String command = script.get(i);
            //数据
            if(command.startsWith(OPERATION_DATA_PREFIX)){
                stack.push(getDataFromOperationData(command));
            }else if(command.startsWith(OPERATION_CODE_PREFIX)){
                if(OPERATION_CODE_DUPLICATE.equals(command)){
                    stack.push(stack.peek());
                }else if(OPERATION_CODE_PUBLIC_KEY_TO_CLASSIC_ADDRESS.equals(command)){
                    String top = stack.peek();
                    String address = AccountUtil.stringAddressFrom(new StringPublicKey(top)).getValue();
                    stack.pop();
                    stack.push(address);
                }else if(OPERATION_CODE_EQUAL_VERIFY.equals(command)){
                    if(!stack.pop().equals(stack.pop())){
                        throw new ExecuteScriptException("脚本执行失败");
                    }
                }else if(OPERATION_CODE_CHECK_SIGN.equals(command)){
                    String publicKey = stack.pop();
                    String sign = stack.pop();
                    boolean verifySignatureSuccess = AccountUtil.verifySignature(new StringPublicKey(publicKey), TransactionTool.getSignatureData(transactionEnvironment),sign);
                    if(!verifySignatureSuccess){
                        throw new ExecuteScriptException("脚本执行失败");
                    }
                    stack.push(String.valueOf(Boolean.TRUE));
                }else {
                    throw new ExecuteScriptException("没有指令");
                }
            }else {
                throw new ExecuteScriptException("指令错误");
            }
        }
        return stack;
    }

    /**
     * 操作数移除操作数前缀，返回真实的操作数
     */
    private static String getDataFromOperationData(String operationData){
        return operationData.substring(OPERATION_DATA_PREFIX.length());
    }

    /**
     * 将操作数前缀加到真实的操作数前面
     */
    private static String getOperationDataFromData(String data){
        return OPERATION_DATA_PREFIX + data;
    }


    public static Script createPayToClassicAddressScript(ScriptKey scriptKey, ScriptLock scriptLock) {
        Script script = new Script();
        script.addAll(scriptKey);
        script.addAll(scriptLock);
        return script;
    }

    public static ScriptKey createPayToClassicAddressInputScript(String sign,String publicKey) {
        ScriptKey script = new ScriptKey();
        script.add(getOperationDataFromData(sign));
        script.add(getOperationDataFromData(publicKey));
        return script;
    }

    public static ScriptLock createPayToClassicAddressOutputScript(String address) {
        ScriptLock script = new ScriptLock();
        script.add(OPERATION_CODE_DUPLICATE);
        script.add(OPERATION_CODE_PUBLIC_KEY_TO_CLASSIC_ADDRESS);
        script.add(getOperationDataFromData(address));
        script.add(OPERATION_CODE_EQUAL_VERIFY);
        script.add(OPERATION_CODE_CHECK_SIGN);
        return script;
    }
}
