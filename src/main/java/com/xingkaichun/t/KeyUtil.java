package com.xingkaichun.t;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class KeyUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyUtil.class);
    private static BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();  

    public KeyContext genPublicKey(String privateKeyString) {
        logger.info("Origin privateKey: {}", privateKeyString);
        KeyContext.KeyContextBuilder builder = KeyContext.builder();
        try {
            byte[] decoded = Base64.getDecoder().decode(privateKeyString);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(keySpec);

            System.out.println("--->PrivateKey"+Base64.getEncoder().encodeToString(privateKey.getEncoded()));

            RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;

            RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            logger.info("Gen publicKey: {}", publicKeyStr);
            builder.privateKeyStr(privateKeyString).privateKey(privateKey).publicKeyStr(publicKeyStr).publicKey(publicKey);
        } catch (Exception e) {
            logger.error("genPublicKey Error:", e);
        }
        return builder.build();
    }

    public String encrypt(PublicKey publicKey, String context) {
        if (publicKey == null) {
            logger.error("KeyUtil.encrypt--context:{}, publicKey is null", context);
            return context;
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA", bouncyCastleProvider);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(context.getBytes());
            return Base64.getEncoder().encodeToString(output);
        } catch (Exception e) {
            logger.error("KeyUtil.encrypt Exception:", e);
            return context;
        }
    }

    public String decrypt(PrivateKey privateKey, String context) {
        if (privateKey == null) {
            logger.error("KeyUtil.decrypt--context:{}, privateKey is null", context);
            return context;
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA", bouncyCastleProvider);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(Base64.getDecoder().decode(context));
            return new String(output);
        } catch (Exception e) {
            logger.error("KeyUtil.decrypt Exception:", e);
            return context;
        }
    }

    public static void main(String[] args) {
        KeyUtil keyUtil = new KeyUtil();
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKKUer0JPGmT6Q3KnpDS5nJO8eSMPBVNwrixd1s4bAYACGFxnzg1xT+TvhmLqxWfj0gX8w2dGKP1PGgwd3F69mTsC+Yn6pAqQOETgn7JqdFHp01YCV5beK0GC7Ev7QSHTVy7PeH3J5ppuOEEd+wOsoP9GztArjgMJfvBA01BL7DxAgMBAAECgYBjQRZ1lc/l/MDJBKwtajP6ESwoBV0g/Goma2GctSNtvlNfeghkPp9/IulpmxUFjHXi44wlAYVrg2oviXdCNnl5RiWUgNfKntoQ96MVZAaOY4HKD06sPZOM2jG8sAGcOk656FeL8KYlad7Kbk/a/Elbujdp3yHWuEgnpT9E7WX2gQJBANYqkYN4mmP5a7p0mMmfjEJmo/IHHFYO3vKcHhoHdOnOi5MOMoY5sE5hNGQAINRKDgfSZ8q+/C43paDYWZrYcPkCQQDCVlPfMYGJfWx0u8MSEja54NeP87k/UfY4m1V/BUHWN62gUDaF3Uc6oRz9DQwlOiYo9qT3Zs7gNAc8uXFplrW5AkEAznlFxrlsJ3xctusYLjIqmA26e2kNkY5OtRl8D94mgg8GEyV54lwVtMsUJmDVRbWLp1DbjeTo3Wn6vYI3iQioiQJAaLtLchJlBCrC41o5M6j7M0t4AI1RvU03i6Qy/ERiCcdx296+s3/gHjmrvLhmXj2rSRI7L1WJkgyYBeLOux/MiQJAUgk4Oml7T//xhhb+E3A2BBKkhnMuHHp18jHw31EqAzCQtzrcZg+cA4woISsH18sqo/iy8qeW00WCIVizVVdoRg==";
        KeyContext keyContext = keyUtil.genPublicKey(privateKey);
        String originStr = "thisisatest";
        String encryptStr = keyUtil.encrypt(keyContext.getPublicKey(), originStr);
        String decryptStr = keyUtil.decrypt(keyContext.getPrivateKey(), encryptStr);
        System.out.println("~~~~~~~~~~originStr : " + originStr);
        System.out.println("~~~~~~~~~~encryptStr : " + encryptStr);
        System.out.println("~~~~~~~~~~decryptStr : " + decryptStr);


        keyContext = keyUtil.genPublicKey(privateKey);
        originStr = "thisisatest";
        encryptStr = keyUtil.encrypt(keyContext.getPublicKey(), originStr);
        decryptStr = keyUtil.decrypt(keyContext.getPrivateKey(), encryptStr);
        System.out.println("~~~~~~~~~~originStr : " + originStr);
        System.out.println("~~~~~~~~~~encryptStr : " + encryptStr);
        System.out.println("~~~~~~~~~~decryptStr : " + decryptStr);
    }

}