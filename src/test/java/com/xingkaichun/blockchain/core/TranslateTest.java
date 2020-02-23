package com.xingkaichun.blockchain.core;

import com.google.gson.Gson;
import com.xingkaichun.blockchain.core.dto.DtoUtils;
import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.dto.TransactionInputDTO;
import com.xingkaichun.blockchain.core.dto.TransactionOutputDTO;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class TranslateTest {

    @org.junit.Test
    public void test() throws Exception {
        String blockchainPath = "D:\\logs\\hellowordblockchain\\" ;
        String minerPublicKeyString = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainPath,minerPublicKeyString);
        blockChainCore.start();


        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(TransactionType.MINER);
        ArrayList<TransactionInputDTO> inputs = new ArrayList<>();
        transactionDTO.setInputs(inputs);
        ArrayList<TransactionOutputDTO> outputs = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setTransactionOutputUUID(UUID.randomUUID().toString());
        transactionOutputDTO.setAddress("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        transactionOutputDTO.setValue(new BigDecimal("20"));
        transactionDTO.setOutputs(outputs);
        DtoUtils.signature(transactionDTO,new PrivateKeyString("zzzzzzzzzzzzzzzzzzzz"));

        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        int height = blockChainDataBase.findTailBlock().getHeight();
        for(int i=1;i<=height;i++){
            Block block = blockChainDataBase.findBlockByBlockHeight(i);
            System.out.println(new Gson().toJson(block));
        }
    }
}
