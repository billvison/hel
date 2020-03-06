package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class Test3 {

    public static void main(String[] args) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        String algorithmName = "ECDSA";
        String providerName = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
        String curveName = "secp256k1";
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithmName,providerName);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(curveName);
        keyGen.initialize(ecSpec, random);
        KeyPair keyPair = keyGen.generateKeyPair();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

        String encodePrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String encodePublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        System.out.println(encodePrivateKey);
        System.out.println(encodePublicKey);
        System.out.println(11111111);
        String msg = "11111111";
        byte[] sig = CipherUtil.applyECDSASig(privateKey,msg);
        System.out.println(CipherUtil.verifyECDSASig(publicKey,msg,sig));


    }
}
