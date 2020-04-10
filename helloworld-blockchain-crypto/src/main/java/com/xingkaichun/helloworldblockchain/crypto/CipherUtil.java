package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * 密码学工具类
 */
public class CipherUtil {

    /**
     * ECDSA签名
     */
    public static String applyECDSASig(StringPrivateKey stringPrivateKey, String data) {
        try {
            PrivateKey privateKey = KeyUtil.convertStringPrivateKeyToPrivateKey(stringPrivateKey);
            byte[] bytesSignature = applyECDSASig(privateKey,data.getBytes());
            String strSignature = Base64.getEncoder().encodeToString(bytesSignature);
            return strSignature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECDSA签名验证
     */
    public static boolean verifyECDSASig(StringPublicKey senderStringPublicKey, String data, String strSignature) {
        try {
            byte[] bytesSignature = Base64.getDecoder().decode(strSignature);
            PublicKey publicKey = KeyUtil.convertStringPublicKeyToPublicKey(senderStringPublicKey);
            return verifyECDSASig(publicKey,data.getBytes(),bytesSignature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sha256消息摘要
     */
    public static String applySha256(String inputs) {
        return applySha256(inputs.getBytes());
    }

    /**
     * RipeMD160消息摘要
     */
    public static String ripeMD160(String data) throws Exception{
        byte[] ripeMD160Data = ripeMD160(data.getBytes());
        return new String(ripeMD160Data);
    }







    /**
     * ECDSA签名
     */
    private static byte[] applyECDSASig(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("ECDSA", "BC");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] sign = signature.sign();
        return sign;
    }

    /**
     * ECDSA签名验证
     */
    private static boolean verifyECDSASig(PublicKey publicKey, byte[] data, byte[] signature) throws Exception {
        Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);
        return ecdsaVerify.verify(signature);
    }

    /**
     * RipeMD160消息摘要
     */
    private static byte[] ripeMD160(byte[] data) throws Exception{
        MessageDigest md = MessageDigest.getInstance("RipeMD160");
        return md.digest(data);
    }

    /**
     * Sha256消息摘要
     */
    private static String applySha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
