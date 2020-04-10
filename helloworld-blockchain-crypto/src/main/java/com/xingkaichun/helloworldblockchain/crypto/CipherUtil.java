package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * 密码学工具类
 */
public class CipherUtil {

    /**
     * ECDSA签名
     */
    public static byte[] applyECDSASig(StringPrivateKey stringPrivateKey, String data) {
        try {
            PrivateKey privateKey = EcKeyUtil.convertStringPrivateKeyToPrivateKey(stringPrivateKey);
            return applyECDSASig(privateKey,data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECDSA签名验证
     */
    public static boolean verifyECDSASig(StringPublicKey senderStringPublicKey, String data, byte[] signature) {
        try {
            PublicKey publicKey = EcKeyUtil.convertStringPublicKeyToPublicKey(senderStringPublicKey);
            return verifyECDSASig(publicKey,data,signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sha256消息摘要
     */
    public static String applySha256(byte[] input) {
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

    /**
     * RipeMD160消息摘要
     */
    public static byte[] ripeMD160(byte[] data) throws Exception{
        MessageDigest md = MessageDigest.getInstance("RipeMD160");
        return md.digest(data);
    }

    public static String ripeMD160(String data) throws Exception{
        MessageDigest md = MessageDigest.getInstance("RipeMD160");
        return new String(md.digest(data.getBytes()));
    }

    public static String applySha256(String inputs) {
        return applySha256(inputs.getBytes());
    }




    /**
     * ECDSA签名
     */
    private static byte[] applyECDSASig(PrivateKey privateKey, String data) throws Exception {
        Signature signature = Signature.getInstance("ECDSA", "BC");
        signature.initSign(privateKey);
        byte[] strByte = data.getBytes();
        signature.update(strByte);
        byte[] sign = signature.sign();
        return sign;
    }

    /**
     * ECDSA签名验证
     */
    private static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) throws Exception {
        Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data.getBytes());
        return ecdsaVerify.verify(signature);
    }
}
