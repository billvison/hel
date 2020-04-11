package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * 密码学工具类
 */
public class SHA256Util {

    /**
     * 首先应用sha256生成消息摘要，然后将摘要转为十六进制字符串
     */
    public static String applySha256HexString(String inputs) {
        byte[] sha256Digest = applySha256(inputs.getBytes());
        return HexUtil.bytesToHexString(sha256Digest);
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
