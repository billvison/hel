package com.xingkaichun.helloworldblockchain.core;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.model.Block;

public class PrintBlockTest {

    @org.junit.Test
    public void test() throws Exception {
        String blockchainPath = "D:\\logs\\hellowordblockchain\\" ;
        String createBlockChainCore = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainPath,createBlockChainCore);
        blockChainCore.start();

        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        int height = blockChainDataBase.findTailBlock().getHeight();
        for(int i=1;i<=height;i++){
            Block block = blockChainDataBase.findBlockByBlockHeight(i);
            System.out.println(new Gson().toJson(block));
        }
    }
}
