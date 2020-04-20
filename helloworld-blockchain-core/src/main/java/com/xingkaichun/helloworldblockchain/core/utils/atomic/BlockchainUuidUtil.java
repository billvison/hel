package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.google.common.base.Joiner;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.Base58Util;
import com.xingkaichun.helloworldblockchain.crypto.Base64Util;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionOutputDTO;

import java.util.ArrayList;
import java.util.List;

public class BlockchainUuidUtil {

    /**
     * 校验交易的UUID的格式是否正确
     */
    public static boolean isTransactionUuidRight(Transaction transaction) {
        String transactionUUID = transaction.getTransactionUUID();
        if(!transactionUUID.endsWith(String.valueOf(transaction.getTimestamp()))){
            return false;
        }
        String targetTransactionUUID = calculateTransactioUUID(transaction);
        return transactionUUID.equals(targetTransactionUUID);
    }

    /**
     * 校验交易输出的UUID的格式是否正确
     */
    public static boolean isTransactionOutputUuidRight(Transaction transaction,TransactionOutput output) {
        String transactionOutputUUID = output.getTransactionOutputUUID();
        if(!transactionOutputUUID.endsWith(String.valueOf(transaction.getTimestamp()))){
            return false;
        }
        String targetTransactionOutputUUID = calculateTransactionOutputUUID(output,transaction.getTimestamp());
        return transactionOutputUUID.equals(targetTransactionOutputUUID);
    }

    public static String calculateTransactioUUID(Transaction transaction){
        List<String> outputUuidList = new ArrayList<>();
        for(TransactionOutput transactionOutput:transaction.getOutputs()){
            outputUuidList.add(transactionOutput.getTransactionOutputUUID());
        }
        return calculateTransactioUUID(transaction.getTimestamp(),outputUuidList);
    }

    public static String calculateTransactioUUID(TransactionDTO transaction){
        List<String> outputUuidList = new ArrayList<>();
        for(TransactionOutputDTO transactionOutputDTO:transaction.getOutputs()){
            outputUuidList.add(transactionOutputDTO.getTransactionOutputUUID());
        }
        return calculateTransactioUUID(transaction.getTimestamp(),outputUuidList);
    }

    public static String calculateTransactioUUID(long currentTimeMillis,List<String> outputUuidList){
        String forHash = "";
        forHash += "[" + currentTimeMillis + "]";
        forHash += "[" + Joiner.on(" ").join(outputUuidList) + "]";
        byte[] sha256 = SHA256Util.applySha256(forHash.getBytes());
        String base64Encode = Base64Util.encode(sha256);
        return base64Encode + currentTimeMillis;
    }

    public static String calculateTransactionOutputUUID(TransactionOutput output, long currentTimeMillis) {
        return calculateTransactionOutputUUID(currentTimeMillis,output.getStringAddress().getValue(),output.getValue().toPlainString(),output.getScriptLock());
    }

    public static String calculateTransactionOutputUUID(TransactionOutputDTO transactionOutputDTO, long currentTimeMillis) {
        return calculateTransactionOutputUUID(currentTimeMillis,transactionOutputDTO.getAddress(),transactionOutputDTO.getValue(),transactionOutputDTO.getScriptLock());
    }

    public static String calculateTransactionOutputUUID(long currentTimeMillis, String address, String value, List<String> scriptLock) {
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
