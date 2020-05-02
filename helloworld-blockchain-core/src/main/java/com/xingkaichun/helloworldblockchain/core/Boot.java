package com.xingkaichun.helloworldblockchain.core;

/**
 * 启动类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class Boot {


    public static void main(String[] args) throws Exception {
        String blockchainPath = "D:\\logs\\hellowordblockchain\\" ;
        String minerAddress = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainPath,minerAddress);
        blockChainCore.start();
    }
}
