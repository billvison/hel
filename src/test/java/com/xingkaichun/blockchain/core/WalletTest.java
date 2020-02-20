package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.wallet.Wallet;
import com.xingkaichun.blockchain.core.utils.atomic.WalletUtil;

public class WalletTest {

    @org.junit.Test
    public void test(){
        Wallet wallet = WalletUtil.generateWallet();
        System.out.println("私钥" + wallet.getPrivateKeyString().getValue());
        System.out.println("公钥" + wallet.getPublicKeyString().getValue());
    }
}
