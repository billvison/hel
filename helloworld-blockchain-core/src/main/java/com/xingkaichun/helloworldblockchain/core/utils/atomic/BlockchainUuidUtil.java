package com.xingkaichun.helloworldblockchain.core.utils.atomic;

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
 * UUID工具类
 */
public class BlockchainUuidUtil {

    /**
     * 校验交易的UUID的格式是否正确
     */
    public static boolean isTransactionUuidRight(Transaction transaction) {
        String transactionUUID = transaction.getTransactionUUID();
        String targetTransactionUUID = calculateTransactionUUID(transaction);
        return transactionUUID.equals(targetTransactionUUID);
    }

    /**
     * 校验交易输出的UUID的格式是否正确
     */
    public static boolean isTransactionOutputUuidRight(Transaction transaction,TransactionOutput output) {
        String transactionOutputUUID = output.getTransactionOutputUUID();
        String targetTransactionOutputUUID = calculateTransactionOutputUUID(transaction,output);
        return transactionOutputUUID.equals(targetTransactionOutputUUID);
    }

    /**
     * 计算交易UUID
     */
    public static String calculateTransactionUUID(Transaction transaction){
        List<String> inputUuidList = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInput transactionInput:inputs){
                inputUuidList.add(transactionInput.getUnspendTransactionOutput().getTransactionOutputUUID());
            }
        }
        List<String> outputUuidList = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null && outputs.size()!=0){
            for(TransactionOutput transactionOutput:outputs){
                outputUuidList.add(transactionOutput.getTransactionOutputUUID());
            }
        }
        return calculateTransactionUUID(transaction.getTimestamp(),inputUuidList,outputUuidList);
    }

    /**
     * 计算交易UUID
     */
    public static String calculateTransactionUUID(TransactionDTO transaction){
        List<String> inputUuidList = new ArrayList<>();
        List<TransactionInputDTO> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInputDTO transactionInputDTO:inputs){
                inputUuidList.add(transactionInputDTO.getUnspendTransactionOutputUUID());
            }
        }
        List<String> outputUuidList = new ArrayList<>();
        List<TransactionOutputDTO> outputs = transaction.getOutputs();
        for(TransactionOutputDTO transactionOutputDTO:outputs){
            outputUuidList.add(transactionOutputDTO.getTransactionOutputUUID());
        }
        return calculateTransactionUUID(transaction.getTimestamp(),inputUuidList,outputUuidList);
    }

    /**
     * 计算交易UUID
     */
    private static String calculateTransactionUUID(long currentTimeMillis,List<String> inputUtxoUuids,List<String> outputUtxoUuids){
        String data = "";
        if(inputUtxoUuids != null && inputUtxoUuids.size()!=0){
            data += "[" + Joiner.on(",").join(inputUtxoUuids) + "]";
        }
        if(outputUtxoUuids != null && outputUtxoUuids.size()!=0){
            data += "[" + Joiner.on(",").join(outputUtxoUuids) + "]";
        }
        byte[] byteSha256 = SHA256Util.applySha256(data.getBytes());
        String base64Encode = Base64Util.encode(byteSha256);
        return base64Encode + currentTimeMillis;
    }

    /**
     * 计算交易输出UUID
     */
    public static String calculateTransactionOutputUUID(Transaction transaction,TransactionOutput output) {
        return calculateTransactionOutputUUID(transaction.getTimestamp(),output.getStringAddress().getValue(),output.getValue().toPlainString(),output.getScriptLock());
    }

    /**
     * 计算交易输出UUID
     */
    public static String calculateTransactionOutputUUID(TransactionDTO transactionDTO,TransactionOutputDTO transactionOutputDTO) {
        return calculateTransactionOutputUUID(transactionDTO.getTimestamp(),transactionOutputDTO.getAddress(),transactionOutputDTO.getValue(),transactionOutputDTO.getScriptLock());
    }

    /**
     * 计算交易输出UUID
     */
    private static String calculateTransactionOutputUUID(long currentTimeMillis, String address, String value, List<String> scriptLock) {
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
