package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.base.Joiner;
import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.Base58Util;
import com.xingkaichun.helloworldblockchain.crypto.Base64Util;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionTool {

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
    public static String signature(String privateKey, Transaction transaction) {
        String strSignature = AccountUtil.signature(privateKey, getSignatureData(transaction));
        return strSignature;
    }

    /**
     * 验证脚本
     */
    public static boolean verifyScript(Transaction transaction) throws Exception {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInput transactionInput:inputs){
                Script payToClassicAddressScript = StackBasedVirtualMachine.createPayToClassicAddressScript(transactionInput.getScriptKey(),transactionInput.getUnspendTransactionOutput().getScriptLock());
                StackBasedVirtualMachine stackBasedVirtualMachine = new StackBasedVirtualMachine();
                ScriptExecuteResult scriptExecuteResult = stackBasedVirtualMachine.executeScript(transaction,payToClassicAddressScript);
                return Boolean.valueOf(scriptExecuteResult.pop());
            }
        }
        return true;
    }




    /**
     * 校验交易的哈希是否正确
     */
    public static boolean isTransactionHashRight(Transaction transaction) {
        String transactionHash = transaction.getTransactionHash();
        String targetTransactionHash = calculateTransactionHash(transaction);
        return transactionHash.equals(targetTransactionHash);
    }

    /**
     * 校验交易输出的哈希是否正确
     */
    public static boolean isTransactionOutputHashRight(Transaction transaction,TransactionOutput output) {
        String transactionOutputHash = output.getTransactionOutputHash();
        String targetTransactionOutputHash = calculateTransactionOutputHash(transaction,output);
        return transactionOutputHash.equals(targetTransactionOutputHash);
    }

    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(Transaction transaction){
        List<String> inputHashList = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInput transactionInput:inputs){
                inputHashList.add(transactionInput.getUnspendTransactionOutput().getTransactionOutputHash());
            }
        }
        List<String> outputHashList = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null && outputs.size()!=0){
            for(TransactionOutput transactionOutput:outputs){
                outputHashList.add(transactionOutput.getTransactionOutputHash());
            }
        }
        return calculateTransactionHash(transaction.getTimestamp(),transaction.getTransactionType().getCode(),inputHashList,outputHashList);
    }

    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(TransactionDTO transactionDTO){
        List<String> inputHashList = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInputDTO transactionInputDTO:inputs){
                inputHashList.add(transactionInputDTO.getUnspendTransactionOutputHash());
            }
        }
        List<String> outputHashList = new ArrayList<>();
        List<TransactionOutputDTO> outputs = transactionDTO.getOutputs();
        for(TransactionOutputDTO transactionOutputDTO:outputs){
            outputHashList.add(calculateTransactionOutputHash(transactionDTO,transactionOutputDTO));
        }
        return calculateTransactionHash(transactionDTO.getTimestamp(),transactionDTO.getTransactionTypeCode(),inputHashList,outputHashList);
    }

    /**
     * 计算交易哈希
     */
    private static String calculateTransactionHash(long currentTimeMillis,int transactionTypeCode,List<String> inputHashList,List<String> outputHashList){
        String data = "";
        data += "[" + transactionTypeCode + "]";
        if(inputHashList != null && inputHashList.size()!=0){
            data += "[" + Joiner.on(",").join(inputHashList) + "]";
        }
        if(outputHashList != null && outputHashList.size()!=0){
            data += "[" + Joiner.on(",").join(outputHashList) + "]";
        }
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(data));
        String base64Encode = Base64Util.encode(sha256Digest);
        return base64Encode + currentTimeMillis;
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(Transaction transaction,TransactionOutput output) {
        return calculateTransactionOutputHash(transaction.getTimestamp(),output.getAddress(),output.getValue().toPlainString(),output.getScriptLock());
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(TransactionDTO transactionDTO,TransactionOutputDTO transactionOutputDTO) {
        return calculateTransactionOutputHash(transactionDTO.getTimestamp(),transactionOutputDTO.getAddress(),transactionOutputDTO.getValue(),transactionOutputDTO.getScriptLock());
    }

    /**
     * 计算交易输出哈希
     */
    private static String calculateTransactionOutputHash(long currentTimeMillis, String address, String value, List<String> scriptLock) {
        String forHash = "";
        forHash += "[" + currentTimeMillis + "]";
        forHash += "[" + address + "]";
        forHash += "[" + value + "]";
        forHash += "[" + Joiner.on(" ").join(scriptLock) + "]";
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(forHash));
        String base58Encode = Base58Util.encode(sha256Digest);
        return base58Encode + currentTimeMillis;
    }
}
