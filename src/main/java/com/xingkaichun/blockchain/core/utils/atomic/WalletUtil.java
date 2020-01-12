package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.wallet.Wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

public class WalletUtil {

    public static Wallet loadWallet(PrivateKeyString privateKeyString, PublicKeyString publicKeyString){
        try {
            Wallet wallet = new Wallet(privateKeyString,publicKeyString);
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
            PublicKeyString publicKeyString = KeyUtil.convertPublicKeyToPublicKeyString(keyPair.getPublic());
            PrivateKeyString privateKeyString = KeyUtil.convertPrivateKeyToPrivateKeyString(keyPair.getPrivate());
            Wallet wallet = new Wallet(privateKeyString,publicKeyString);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
