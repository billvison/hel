package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BcBigIntegerToPrivateKey;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.KeyUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.WalletUtil;
import org.junit.Assert;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class KeyUtilTest {


    @Test
    public void test() throws Exception {
        Wallet wallet = WalletUtil.generateWallet();
        PrivateKey privateKey = KeyUtil.convertStringPrivateKeyToPrivateKey(wallet.getStringPrivateKey());

        PublicKey publicKey = KeyUtil.convertStringPublicKeyToPublicKey(wallet.getStringPublicKey());
        //测试这个私钥产生的公钥是否正确
        ECPublicKey publicKey1 = KeyUtil.publicFromPrivate((java.security.interfaces.ECPrivateKey) privateKey);

        Assert.assertEquals(KeyUtil.convertPublicKeyToStringPublicKey(publicKey).getValue(),KeyUtil.convertPublicKeyToStringPublicKey(publicKey1).getValue());

        PrivateKey privateKey10 = BcBigIntegerToPrivateKey.getPrivateKeyFromECBigIntAndCurve(((ECPrivateKey) privateKey).getS(),"secp256k1");
    }
}
