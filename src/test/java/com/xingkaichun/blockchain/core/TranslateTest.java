package com.xingkaichun.blockchain.core;

import com.google.gson.Gson;
import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.dto.TransactionOutputDTO;
import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class TranslateTest {

    @org.junit.Test
    public void test() throws Exception {
        String dbPath = "D:\\logs\\hellowordblockchain\\" ;
        String minerPublicKeyString = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl(dbPath+"BlockChainDataBase",incentive,consensus);

        TransactionDataBase transactionDataBase = new TransactionDataBaseDefaultImpl();

        MinerTransactionDtoDataBase minerTransactionDtoDataBase = new MinerTransactionDtoDtoDataBaseDefaultImpl(dbPath+"MinerTransactionDtoDataBase",transactionDataBase);
        PublicKeyString minerPublicKey = new PublicKeyString(minerPublicKeyString);
        Miner miner = new MinerDefaultImpl(blockChainDataBase, minerTransactionDtoDataBase,minerPublicKey);

        SynchronizerDataBase synchronizerDataBase = new SynchronizerDataBaseDefaultImpl(dbPath+"Synchronizer","otherNodeBlock.data",transactionDataBase);
        BlockChainDataBase blockChainDataBaseDuplicate = new BlockChainDataBaseDefaultImpl(dbPath+"BlockChainDataBaseDuplicate",incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,blockChainDataBaseDuplicate, synchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCoreImpl();
        blockChainCore.setMiner(miner);
        blockChainCore.setSynchronizer(synchronizer);
        blockChainCore.setBlockChainDataBase(blockChainDataBase);
        blockChainCore.setConsensus(consensus);
        blockChainCore.setIncentive(incentive);


        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(TransactionType.MINER);
        ArrayList<String> inputs = new ArrayList<>();
        transactionDTO.setInputs(inputs);
        ArrayList<TransactionOutputDTO> outputs = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setTransactionOutputUUID(UUID.randomUUID().toString());
        transactionOutputDTO.setReciepient(new PublicKeyString("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        transactionOutputDTO.setValue(new BigDecimal("20"));
        transactionDTO.setOutputs(outputs);
        transactionDTO.setSignature(TransactionUtil.signature(transactionDTO,new PrivateKeyString("MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgilXZ39cKVHuzFNjUaZSIPfBh8qWxgHLjKupFPuAezymgBwYFK4EEAAqhRANCAASvClummn+R3t2Ls1dVzHJM93htymb1lACtvIxXbi95Xh/MOcayk808aH3nfQaeE/JwaIQoYsM1yFOLJDn7NZOU")));

        int height = blockChainDataBase.findTailBlock().getHeight();
        for(int i=1;i<=height;i++){
            Block block = blockChainDataBase.findBlockByBlockHeight(i);
            System.out.println(new Gson().toJson(block));
        }
    }
}
