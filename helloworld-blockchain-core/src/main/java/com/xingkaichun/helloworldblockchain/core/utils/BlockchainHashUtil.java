package com.xingkaichun.helloworldblockchain.core.utils;

import com.google.common.base.Joiner;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.Base58Util;
import com.xingkaichun.helloworldblockchain.crypto.Base64Util;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionOutputDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 哈希工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainHashUtil {

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
        return calculateTransactionHash(transaction.getTimestamp(),inputHashList,outputHashList,transaction.getMessages());
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
            outputHashList.add(BlockchainHashUtil.calculateTransactionOutputHash(transactionDTO,transactionOutputDTO));
        }
        return calculateTransactionHash(transactionDTO.getTimestamp(),inputHashList,outputHashList,transactionDTO.getMessages());
    }

    /**
     * 计算交易哈希
     */
    private static String calculateTransactionHash(long currentTimeMillis,List<String> inputHashList,List<String> outputHashList,List<String> messageList){
        String data = "";
        if(inputHashList != null && inputHashList.size()!=0){
            data += "[" + Joiner.on(",").join(inputHashList) + "]";
        }
        if(outputHashList != null && outputHashList.size()!=0){
            data += "[" + Joiner.on(",").join(outputHashList) + "]";
        }
        if(messageList != null && messageList.size()!=0){
            data += "[" + Joiner.on(",").join(messageList) + "]";
        }
        byte[] byteSha256 = SHA256Util.applySha256(data.getBytes());
        String base64Encode = Base64Util.encode(byteSha256);
        return base64Encode + currentTimeMillis;
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(Transaction transaction,TransactionOutput output) {
        return calculateTransactionOutputHash(transaction.getTimestamp(),output.getStringAddress().getValue(),output.getValue().toPlainString(),output.getScriptLock());
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
        byte[] sha256 = SHA256Util.applySha256(forHash.getBytes());
        String base58Encode = Base58Util.encode(sha256);
        return base58Encode + currentTimeMillis;
    }
}
