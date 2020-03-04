package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.google.common.io.BaseEncoding;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;


public class BCECUtil2 {


    public static void main(String[] args) throws InvalidKeySpecException, NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        // Generate Keys
        ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyPairGenerator.initialize(ecGenSpec, new SecureRandom());
        java.security.KeyPair pair = keyPairGenerator.generateKeyPair();
        ECPrivateKey privateKey = (ECPrivateKey) pair.getPrivate();
        ECPublicKey publicKeyExpected = (ECPublicKey) pair.getPublic();

        // Expected public key
        System.out.println("Expected Public Key: " + BaseEncoding.base64Url().encode(publicKeyExpected.getEncoded()));

        // Generate public key from private key
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        org.bouncycastle.jce.spec.ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");

        ECPoint Q = ecSpec.getG().multiply(privateKey.getD());
        byte[] publicDerBytes = Q.getEncoded(false);

        ECPoint point = ecSpec.getCurve().decodePoint(publicDerBytes);
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
        ECPublicKey publicKeyGenerated = (ECPublicKey) keyFactory.generatePublic(pubSpec);

        // Generated public key from private key
        System.out.println("Generated Public Key: " + BaseEncoding.base64Url().encode(publicKeyGenerated.getEncoded()));

/*        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(privateKey.getD(),ecSpec);
        java.security.interfaces.ECPrivateKey ecPrivateKeyGenerated = (ECPrivateKey) keyFactory.generatePrivate(ecPrivateKeySpec);*/




    }
}
