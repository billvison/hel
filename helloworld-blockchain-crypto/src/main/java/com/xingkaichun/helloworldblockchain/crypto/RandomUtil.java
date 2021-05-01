package com.xingkaichun.helloworldblockchain.crypto;

import java.util.Random;

/**
 * 随机数工具
 *
 * @author 邢开春 409060350@qq.com
 */
public class RandomUtil {

    private static final Random RANDOM = new Random();

    public static String random32BytesReturnHexadecimal(){
        byte[] randomBytes = random32Bytes();
        return HexUtil.bytesToHexString(randomBytes);
    }

    public static byte[] random32Bytes(){
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }
}
