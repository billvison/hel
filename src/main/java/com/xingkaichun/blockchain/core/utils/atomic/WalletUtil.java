package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.blockchain.core.model.key.StringPublicKey;
import com.xingkaichun.blockchain.core.model.wallet.Wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

public class WalletUtil {

    public static Wallet loadWallet(StringPrivateKey stringPrivateKey, StringPublicKey stringPublicKey){
        try {
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey);
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
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();
            StringPublicKey stringPublicKey = KeyUtil.convertPublicKeyToStringPublicKey(keyPair.getPublic());
            StringPrivateKey stringPrivateKey = KeyUtil.convertPrivateKeyToStringPrivateKey(keyPair.getPrivate());
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
