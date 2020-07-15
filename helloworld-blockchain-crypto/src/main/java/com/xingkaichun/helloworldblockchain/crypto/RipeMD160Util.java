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
    public static byte[] applyRipeMD160(byte[] data) {
        try {
            MessageDigest ripeMD160Digest = MessageDigest.getInstance("RipeMD160",JavaCryptographyExtensionProviderUtil.getBouncyCastleProviderName());
            return ripeMD160Digest.digest(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
