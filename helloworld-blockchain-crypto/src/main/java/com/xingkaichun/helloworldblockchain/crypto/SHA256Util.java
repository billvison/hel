package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.encoders.Base64;

import java.security.MessageDigest;

/**
 * 密码学工具类
 */
public class SHA256Util {

    /**
     * sha256消息摘要
     */
    public static String applySha256(String inputs) {
        byte[] sha256Digest = applySha256(inputs.getBytes());
        return Base64.toBase64String(sha256Digest);
    }

    /**
     * Sha256消息摘要
     */
    public static byte[] applySha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] sha256Digest = digest.digest(input);
            return sha256Digest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
