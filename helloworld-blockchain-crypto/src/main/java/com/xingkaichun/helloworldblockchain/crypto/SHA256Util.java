package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * 密码学工具类
 */
public class SHA256Util {

    /**
     * sha256消息摘要
     */
    public static String applySha256(String inputs) {
        return applySha256(inputs.getBytes());
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
