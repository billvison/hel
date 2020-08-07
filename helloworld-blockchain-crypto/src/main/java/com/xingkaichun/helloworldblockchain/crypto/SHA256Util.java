package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * Sha256消息摘要工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SHA256Util {

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    /**
     * Sha256消息摘要
     */
    public static byte[] digest(byte[] input) {
        try {
            MessageDigest sha256MessageDigest = MessageDigest.getInstance("SHA-256",JavaCryptographyExtensionProviderUtil.getBouncyCastleProviderName());
            byte[] sha256Digest = sha256MessageDigest.digest(input);
            return sha256Digest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取进行两次Sha256操作的消息摘要
     */
    public static byte[] digestTwice(byte[] input) {
        return digest(digest(input));
    }
}
