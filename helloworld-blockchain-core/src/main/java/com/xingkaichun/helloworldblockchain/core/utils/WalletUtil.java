package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.crypto.EcKeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.HelloWorldEcKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;

public class WalletUtil {

    public static Wallet loadWallet(StringPrivateKey stringPrivateKey, StringPublicKey stringPublicKey, StringAddress stringAddress){
        try {
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey,stringAddress);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Wallet generateWallet(){
        try {
            HelloWorldEcKey helloWorldEcKey = EcKeyUtil.randomHelloWorldEcKey();
            StringPublicKey stringPublicKey = new StringPublicKey(helloWorldEcKey.getHelloWorldPublicEcKey().getEncodePublicKey());
            StringPrivateKey stringPrivateKey = new StringPrivateKey(helloWorldEcKey.getHelloWorldEcPrivateKey().getEncodePrivateKey());
            StringAddress stringAddress = EcKeyUtil.convertStringPublicKeyToStringAddress(stringPublicKey);
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey,stringAddress);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
