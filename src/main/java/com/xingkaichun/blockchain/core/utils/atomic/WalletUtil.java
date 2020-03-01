package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.key.StringAddress;
import com.xingkaichun.blockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.blockchain.core.model.key.StringPublicKey;
import com.xingkaichun.blockchain.core.model.wallet.Wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

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
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            StringPublicKey stringPublicKey = KeyUtil.convertPublicKeyToStringPublicKey(keyPair.getPublic());
            StringPrivateKey stringPrivateKey = KeyUtil.convertPrivateKeyToStringPrivateKey(keyPair.getPrivate());
            StringAddress stringAddress = KeyUtil.convertStringPublicKeyToStringAddress(stringPublicKey);
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey,stringAddress);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
