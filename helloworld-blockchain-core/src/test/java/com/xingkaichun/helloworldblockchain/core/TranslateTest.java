package com.xingkaichun.helloworldblockchain.core;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.dto.*;
import com.xingkaichun.helloworldblockchain.core.utils.DtoUtils;
import com.xingkaichun.helloworldblockchain.model.Block;
import com.xingkaichun.helloworldblockchain.model.key.StringPrivateKey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class TranslateTest {

    @org.junit.Test
    public void test() throws Exception {
        String blockchainPath = "D:\\logs\\hellowordblockchain\\" ;
        String createBlockChainCore = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainPath,createBlockChainCore);
        blockChainCore.start();


        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(TransactionTypeDTO.MINER);
        ArrayList<TransactionInputDTO> inputs = new ArrayList<>();
        transactionDTO.setInputs(inputs);
        ArrayList<TransactionOutputDTO> outputs = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setTransactionOutputUUID(UUID.randomUUID().toString());
        transactionOutputDTO.setAddress("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        transactionOutputDTO.setValue(new BigDecimal("20"));
        transactionDTO.setOutputs(outputs);
        DtoUtils.signature(transactionDTO,new StringPrivateKey("zzzzzzzzzzzzzzzzzzzz"));

        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        int height = blockChainDataBase.findTailBlock().getHeight();
        for(int i=1;i<=height;i++){
            Block block = blockChainDataBase.findBlockByBlockHeight(i);
            System.out.println(new Gson().toJson(block));
        }
    }
}
