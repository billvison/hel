package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {

    public static byte[] decode(PublicKeyString publicKeyString) {
        return Base64.getDecoder().decode(publicKeyString.getValue());
    }

    public static byte[] decode(PrivateKeyString privateKeyString) {
        return Base64.getDecoder().decode(privateKeyString.getValue());
    }

    public static PublicKey convertPublicKeyStringToPublicKey(PublicKeyString publicKeyString) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] publicKeyEncode = decode(publicKeyString);

            final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyEncode);
            final PublicKey pubKey = kf.generatePublic(pubKeySpec);
            return pubKey;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey convertPrivateKeyStringToPrivateKey(PrivateKeyString privateKeyString) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] privateKeyEncode = decode(privateKeyString);

            final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            final PKCS8EncodedKeySpec encPrivKeySpec = new PKCS8EncodedKeySpec(privateKeyEncode);
            final PrivateKey privKey = kf.generatePrivate(encPrivKeySpec);
            return privKey;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKeyString convertPublicKeyToPublicKeyString(PublicKey publicKey) {
        String encode = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        PublicKeyString publicKeyString = new PublicKeyString(encode);
        return publicKeyString;
    }

    public static PrivateKeyString convertPrivateKeyToPrivateKeyString(PrivateKey privateKey) {
        String encode = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        PrivateKeyString privateKeyString = new PrivateKeyString(encode);
        return privateKeyString;
    }
}
