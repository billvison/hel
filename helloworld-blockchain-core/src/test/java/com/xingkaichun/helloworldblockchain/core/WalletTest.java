package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.WalletUtil;

public class WalletTest {

    @org.junit.Test
    public void test(){
        Wallet wallet = WalletUtil.generateWallet();
        System.out.println("私钥" + wallet.getStringPrivateKey().getValue());
        System.out.println("公钥" + wallet.getStringPublicKey().getValue());
    }
}
