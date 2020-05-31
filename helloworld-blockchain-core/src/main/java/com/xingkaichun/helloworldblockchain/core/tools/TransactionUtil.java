package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.script.Script;
import com.xingkaichun.helloworldblockchain.core.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.script.ScriptMachine;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;

import java.math.BigDecimal;
import java.util.List;

/**
 * Transaction工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionUtil {

    /**
     * 交易输入总额
     */
    public static BigDecimal getInputsValue(Transaction transaction) {
        return getInputsValue(transaction.getInputs());
    }
    /**
     * 交易输入总额
     */
    public static BigDecimal getInputsValue(List<TransactionInput> inputs) {
        BigDecimal total = BigDecimal.ZERO;
        if(inputs != null){
            for(TransactionInput i : inputs) {
                if(i.getUnspendTransactionOutput() == null) continue;
                total = total.add(i.getUnspendTransactionOutput().getValue());
            }
        }
        return total;
    }



    /**
     * 交易输出总额
     */
    public static BigDecimal getOutputsValue(Transaction transaction) {
        return getOutputsValue(transaction.getOutputs());
    }
    /**
     * 交易输出总额
     */
    public static BigDecimal getOutputsValue(List<TransactionOutput> outputs) {
        BigDecimal total = BigDecimal.ZERO;
        if(outputs != null){
            for(TransactionOutput o : outputs) {
                total = total.add(o.getValue());
            }
        }
        return total;
    }



    /**
     * 获取用于签名的交易数据
     * @return
     */
    public static String getSignatureData(Transaction transaction) {
        String data = transaction.getTransactionHash();
        return data;
    }

    /**
     * 交易签名
     */
    public static String signature(StringPrivateKey stringPrivateKey, Transaction transaction) {
        String strSignature = AccountUtil.signature(stringPrivateKey, getSignatureData(transaction));
        return strSignature;
    }

    /**
     * 签名脚本
     */
    public static boolean verifyScript(Transaction transaction) throws Exception {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInput transactionInput:inputs){
                Script payToClassicAddressScript = ScriptMachine.createPayToClassicAddressScript(transactionInput.getScriptKey(),transactionInput.getUnspendTransactionOutput().getScriptLock());
                ScriptMachine scriptMachine = new ScriptMachine();
                ScriptExecuteResult scriptExecuteResult = scriptMachine.executeScript(transaction,payToClassicAddressScript);
                return Boolean.valueOf(scriptExecuteResult.pop());
            }
        }
        return true;
    }
}
