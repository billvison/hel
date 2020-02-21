package com.xingkaichun.blockchain.core.dto;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.utils.atomic.CipherUtil;
import com.xingkaichun.blockchain.core.utils.atomic.KeyUtil;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DtoUtils {


    public static Transaction classCast(BlockChainDataBase blockChainDataBase, TransactionDTO transactionDTO) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(transaction.getTimestamp());
        transaction.setTransactionUUID(transactionDTO.getTransactionUUID());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setSignature(transactionDTO.getSignature());

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        transaction.setInputs(inputs);
        List<String> dtoInputs = transactionDTO.getInputs();
        if(dtoInputs!=null || dtoInputs.size()!=0){
            for (String dtoInput:dtoInputs){
                TransactionOutput transactionOutput = blockChainDataBase.findUtxoByUtxoUuid(dtoInput);
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspendTransactionOutput(transactionOutput);
                inputs.add(transactionInput);
            }
        }

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        transaction.setOutputs(outputs);
        List<TransactionOutputDTO> dtoOutputs = transactionDTO.getOutputs();
        if(dtoOutputs!=null || dtoOutputs.size()!=0){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = classCast(transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        return transaction;
    }

    public static TransactionOutput classCast(TransactionOutputDTO transactionOutputDTO) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionOutputUUID(transactionOutputDTO.getTransactionOutputUUID());
        transactionOutput.setReciepient(new PublicKeyString(transactionOutputDTO.getReciepient()));
        transactionOutput.setValue(transactionOutputDTO.getValue());
        return transactionOutput;
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
     * 用于签名的数据数据
     * @return
     */
    public static String signatureData(TransactionDTO transactionDTO) throws Exception {
        String data = TransactionUtil.signatureData(transactionDTO.getTimestamp(),transactionDTO.getTransactionUUID(),getInputUtxoIds(transactionDTO),getOutpuUtxoIds(transactionDTO));
        String sha256Data = CipherUtil.applySha256(data);
        return sha256Data;
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
}
