package com.xingkaichun.blockchain.core;


public class Boot {


    public static void main(String[] args) throws Exception {
        String blockchainPath = "D:\\logs\\hellowordblockchain\\" ;
        String minerAddress = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainPath,minerAddress);
        blockChainCore.start();
    }
}
