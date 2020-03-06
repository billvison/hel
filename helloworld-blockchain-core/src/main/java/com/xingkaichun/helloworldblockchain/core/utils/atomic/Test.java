package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class Test {

    public static void main(String[] args) throws Exception {

        BigInteger bigInteger = new BigInteger("100");
        System.out.println(bigInteger);


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

        //用privateKey.getS()恢复私钥
        ECNamedCurveParameterSpec ecCurve = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curveName);
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(privateKey.getS(), ecCurve);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithmName,org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
        ECPrivateKey generatePrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);

        //比较原私钥和恢复的私钥是否相等
        String encodePrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String encodeGeneratePrivateKey = Base64.getEncoder().encodeToString(generatePrivateKey.getEncoded());

        //期望结果是true
        System.out.println(encodePrivateKey.equals(encodeGeneratePrivateKey));
    }
}
