package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.dto.TransactionOutputDTO;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TransactionUtil {

    /**
     * 交易输入总额
     */
    public static BigDecimal getInputsValue(ArrayList<TransactionInput> inputs) {
        BigDecimal total = new BigDecimal("0");
        for(TransactionInput i : inputs) {
            if(i.getUnspendTransactionOutput() == null) continue; //if Transaction can't be found skip it, This behavior may not be optimal.
            total = total.add(i.getUnspendTransactionOutput().getValue());
        }
        return total;
    }

    /**
     * 交易输出总额
     */
    public static BigDecimal getOutputsValue(ArrayList<TransactionOutput> outputs) {
        BigDecimal total = new BigDecimal("0");
        for(TransactionOutput o : outputs) {
            total = total.add(o.getValue());

        }
        return total;
    }

    /**
     * 交易输入总额
     */
    public static BigDecimal getInputsValue(Transaction transaction) {
        return getInputsValue(transaction.getInputs());
    }

    /**
     * 交易输出总额
     */
    public static BigDecimal getOutputsValue(Transaction transaction) {
        return getOutputsValue(transaction.getOutputs());
    }


    /**
     * 用于签名的数据数据
     * @return
     */
    public static String signatureData(Transaction transaction) throws Exception {
        String data = signatureData(transaction.getTimestamp(),transaction.getTransactionUUID(),getInputUtxoIds(transaction),getOutpuUtxoIds(transaction));
        String sha256Data = CipherUtil.applySha256(data);
        return sha256Data;
    }

    /**
     * 用于签名的数据数据
     * @return
     */
    public static String signatureData(TransactionDTO transactionDTO) throws Exception {
        String data = signatureData(transactionDTO.getTimestamp(),transactionDTO.getTransactionUUID(),getInputUtxoIds(transactionDTO),getOutpuUtxoIds(transactionDTO));
        String sha256Data = CipherUtil.applySha256(data);
        return sha256Data;
    }

    /**
     * 用于签名的数据数据
     * @return
     */
    public static String signatureData(long timestamp,String transactionUUID,List<String> inputUtxoUuidList,List<String> outputUtxoUuidList) throws Exception {
        String inputs = "";
        if(inputUtxoUuidList != null){
            for(String input:inputUtxoUuidList){
                inputs += input;
            }
        }
        String outputs = "";
        if(outputUtxoUuidList != null){
            for(String output:outputUtxoUuidList){
                outputs += output;
            }
        }
        String data = timestamp + transactionUUID + inputs + outputs;
        String sha256Data = CipherUtil.applySha256(data);
        return sha256Data;
    }

    public static PublicKeyString getSenderPublicKeyString(Transaction transaction) {
        ArrayList<TransactionInput> inputs = transaction.getInputs();
        if(inputs == null || inputs.size() == 0){
            return null;
        }
        PublicKeyString senderPublicKeyString = inputs.get(0).getUnspendTransactionOutput().getReciepient();
        return senderPublicKeyString;
    }

    /**
     * 交易签名
     */
    public static String signature(Transaction transaction, PrivateKeyString privateKeyString) throws Exception {
        PrivateKey privateKey = KeyUtil.convertPrivateKeyStringToPrivateKey(privateKeyString);
        byte[] bytesSignature = CipherUtil.applyECDSASig(privateKey,signatureData(transaction));
        String strSignature = Base64.getEncoder().encodeToString(bytesSignature);
        return strSignature;
    }
    /**
     * 交易签名
     */
    public static String signature(TransactionDTO transactionDTO, PrivateKeyString privateKeyString) throws Exception {
        PrivateKey privateKey = KeyUtil.convertPrivateKeyStringToPrivateKey(privateKeyString);
        byte[] bytesSignature = CipherUtil.applyECDSASig(privateKey,signatureData(transactionDTO));
        String strSignature = Base64.getEncoder().encodeToString(bytesSignature);
        return strSignature;
    }

    /**
     * 签名验证
     */
    public static boolean verifySignature(Transaction transaction) throws Exception {
        PublicKeyString senderPublicKeyString = getSenderPublicKeyString(transaction);
        PublicKey publicKey = KeyUtil.convertPublicKeyStringToPublicKey(senderPublicKeyString);
        String strSignature = transaction.getSignature();
        byte[] bytesSignature = Base64.getDecoder().decode(strSignature);
        return CipherUtil.verifyECDSASig(publicKey,signatureData(transaction),bytesSignature);
    }

    public static List<String> getInputUtxoIds(Transaction transaction){
        List<String> ids = new ArrayList<>();
        if(transaction.getInputs()==null||ids.size()==0){return ids;}
        for(TransactionInput transactionInput:transaction.getInputs()){
            ids.add(transactionInput.getUnspendTransactionOutput().getTransactionOutputUUID());
        }
        return ids;
    }

    public static List<String> getOutpuUtxoIds(Transaction transaction){
        List<String> ids = new ArrayList<>();
        ArrayList<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs==null || outputs.size()==0){
            return ids;
        }
        for(TransactionOutput transactionOutput:outputs){
            ids.add(transactionOutput.getTransactionOutputUUID());
        }
        return ids;
    }

    public static List<String> getInputUtxoIds(TransactionDTO transactionDTO){
        return transactionDTO.getInputs();
    }

    public static List<String> getOutpuUtxoIds(TransactionDTO transactionDTO){
        List<String> ids = new ArrayList<>();
        List<TransactionOutputDTO> output = transactionDTO.getOutputs();
        if(output==null || output.size()==0){
            return ids;
        }
        for(TransactionOutputDTO transactionOutputDTO:output){
            ids.add(transactionOutputDTO.getTransactionOutputUUID());
        }
        return ids;
    }

    public static boolean isSpendOwnUtxo(Transaction transaction){
        PublicKeyString senderPublicKeyString = getSenderPublicKeyString(transaction);
        ArrayList<TransactionInput> inputs = transaction.getInputs();
        for(TransactionInput input:inputs){
            boolean eq = senderPublicKeyString.getValue().equals(input.getUnspendTransactionOutput().getReciepient().getValue());
            if(!eq){
                return false;
            }
        }
        return true;
    }
}
