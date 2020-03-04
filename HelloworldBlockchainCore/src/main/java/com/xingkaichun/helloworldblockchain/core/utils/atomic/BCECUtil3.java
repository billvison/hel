package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;


public class BCECUtil3 {

    public static PrivateKey getPrivateKeyFromECBigIntAndCurve(BigInteger s, String curveName) throws NoSuchProviderException {

        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(curveName);
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, ecParameterSpec);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");//KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws NoSuchProviderException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        BigInteger priv = new BigInteger("4e0ca9242280b0885ec6afc32e5be544628c5641be3c30b6a36117123324a23a", 16);
        PrivateKey privateKey = BCECUtil3.getPrivateKeyFromECBigIntAndCurve(priv, "secp256r1");
        System.out.println(privateKey);
    }


}