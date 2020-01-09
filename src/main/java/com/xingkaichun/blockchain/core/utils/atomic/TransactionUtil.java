package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
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
        String data = transaction.getTimestamp() + transaction.getTransactionUUID()
                        + getInput_UTXO_Ids(transaction) + getOutput_UTXO_Ids(transaction);
        String sha256Data = CipherUtil.applySha256(data);
        return sha256Data;
    }

    /**
     * 交易签名
     */
    public static byte[] signature(Transaction transaction, PrivateKeyString privateKeyString) throws Exception {
        PrivateKey privateKey = KeyUtil.convertPrivateKeyStringToPrivateKey(privateKeyString);
        return CipherUtil.applyECDSASig(privateKey,signatureData(transaction));
    }

    /**
     * 签名验证
     */
    public static boolean verifySignature(Transaction transaction) throws Exception {
        PublicKey publicKey = KeyUtil.convertPublicKeyStringToPublicKey(getSender(transaction));
        return CipherUtil.verifyECDSASig(publicKey,signatureData(transaction),transaction.getSignature());
    }

    public static List<String> getInput_UTXO_Ids(Transaction transaction){
        List<String> ids = new ArrayList<>();
        if(transaction.getInputs()==null||ids.size()==0){return ids;}
        for(TransactionInput transactionInput:transaction.getInputs()){
            ids.add(transactionInput.getUnspendTransactionOutput().getTransactionOutputUUID());
        }
        return ids;
    }

    public static List<String> getOutput_UTXO_Ids(Transaction transaction){
        List<String> ids = new ArrayList<>();
        if(transaction.getInputs()==null||ids.size()==0){return ids;}
        for(TransactionOutput transactionOutput:transaction.getOutputs()){
            ids.add(transactionOutput.getTransactionOutputUUID());
        }
        return ids;
    }

    public static PublicKeyString getSender(Transaction transaction){
        if(!isOnlyOneSender(transaction)){
            throw new BlockChainCoreException("付款方不是同一人");
        }
        TransactionInput transactionInput = transaction.getInputs().get(0);
        return transactionInput.getUnspendTransactionOutput().getReciepient();
    }

    public static boolean isOnlyOneSender(Transaction transaction){
        ArrayList<TransactionInput> inputs = transaction.getInputs();
        TransactionInput transactionInput = transaction.getInputs().get(0);
        for(TransactionInput input:inputs){
            boolean eq = transactionInput.getUnspendTransactionOutput().getReciepient().equals(input.getUnspendTransactionOutput().getReciepient());
            if(!eq){
                return false;
            }
        }
        return true;
    }
}
