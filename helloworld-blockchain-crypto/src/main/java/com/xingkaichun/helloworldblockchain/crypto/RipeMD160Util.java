package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * RipeMD160消息摘要工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class RipeMD160Util {

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    /**
     * RipeMD160消息摘要
     */
    public static byte[] digest(byte[] data) {
        try {
            MessageDigest ripeMD160MessageDigest = MessageDigest.getInstance("RipeMD160",JavaCryptographyExtensionProviderUtil.getBouncyCastleProviderName());
            byte[] ripeMD160Digest = ripeMD160MessageDigest.digest(data);
            return ripeMD160Digest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
