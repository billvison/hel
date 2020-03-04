package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPrivateKey;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;

public class BcTest {

    public static void main(String[] args) throws Exception {


        Wallet wallet = WalletUtil.generateWallet();
        System.out.println("privateKey"+wallet.getStringPrivateKey().getValue());
        System.out.println("publicKey"+wallet.getStringPublicKey().getValue());

        PrivateKey privateKey = KeyUtil.convertStringPrivateKeyToPrivateKey(wallet.getStringPrivateKey());
        PublicKey publicKey = KeyUtil.convertStringPublicKeyToPublicKey(wallet.getStringPublicKey());

        ECPublicKey publicKey1 = BCECUtil.publicFromPrivate((java.security.interfaces.ECPrivateKey) privateKey);

        System.out.println("publicKey"+KeyUtil.convertPublicKeyToStringPublicKey(publicKey1).getValue());

/*        ECPublicKey publicKey2 = BcECPrivateKeyToPublicKey.publicFromPrivate((java.security.interfaces.ECPrivateKey) privateKey);
        System.out.println("publicKey"+KeyUtil.convertPublicKeyToStringPublicKey(publicKey2).getValue());*/
        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey)privateKey;

        PrivateKey privateKey1 = BcBigIntegerToPrivateKey.getPrivateKeyFromECBigIntAndCurve(bcecPrivateKey.getD(),"secp256k1");
        System.out.println("privateKey1"+KeyUtil.convertPrivateKeyToStringPrivateKey(privateKey1).getValue());

    }
}
