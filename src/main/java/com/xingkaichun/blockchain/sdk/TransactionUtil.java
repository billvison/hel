package com.xingkaichun.blockchain.sdk;

import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.dto.TransactionOutputDTO;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class TransactionUtil {


    public TransactionDTO createTransactionDTO(TransactionType transactionType,ArrayList<String> inputs) throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(transactionType);
        transactionDTO.setInputs(inputs);
        ArrayList<TransactionOutputDTO> outputs = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setTransactionOutputUUID(UUID.randomUUID().toString());
        transactionOutputDTO.setReciepient(new PublicKeyString("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        transactionOutputDTO.setValue(new BigDecimal("20"));
        transactionDTO.setOutputs(outputs);
        transactionDTO.setSignature(com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil.signature(transactionDTO,new PrivateKeyString("MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgilXZ39cKVHuzFNjUaZSIPfBh8qWxgHLjKupFPuAezymgBwYFK4EEAAqhRANCAASvClummn+R3t2Ls1dVzHJM93htymb1lACtvIxXbi95Xh/MOcayk808aH3nfQaeE/JwaIQoYsM1yFOLJDn7NZOU")));
        return transactionDTO;
    }

}
