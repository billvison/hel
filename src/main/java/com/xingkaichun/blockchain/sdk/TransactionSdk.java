package com.xingkaichun.blockchain.sdk;

import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.dto.TransactionOutputDTO;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;

import java.util.ArrayList;
import java.util.UUID;

public class TransactionSdk {


    public TransactionDTO signatureTransactionDTO(TransactionDTO transactionDTO,PrivateKeyString privateKeyString) throws Exception {
        String signature = TransactionUtil.signature(transactionDTO,privateKeyString);
        transactionDTO.setSignature(signature);
        return transactionDTO;
    }

    public TransactionDTO createTransactionDTO(PrivateKeyString privateKeyString,ArrayList<String> inputs,ArrayList<TransactionOutputDTO> outputs) throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(TransactionType.MINER);
        transactionDTO.setInputs(inputs);
        transactionDTO.setOutputs(outputs);
        signatureTransactionDTO(transactionDTO,privateKeyString);
        return transactionDTO;
    }

    public static void main(String[] args) {


        PrivateKeyString privateKeyString = new PrivateKeyString("MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgilXZ39cKVHuzFNjUaZSIPfBh8qWxgHLjKupFPuAezymgBwYFK4EEAAqhRANCAASvClummn+R3t2Ls1dVzHJM93htymb1lACtvIxXbi95Xh/MOcayk808aH3nfQaeE/JwaIQoYsM1yFOLJDn7NZOU");


    }

}
