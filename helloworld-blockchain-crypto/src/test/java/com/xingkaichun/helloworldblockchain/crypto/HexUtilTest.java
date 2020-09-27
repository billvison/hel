package com.xingkaichun.helloworldblockchain.crypto;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class HexUtilTest {

    @Test
    public void bytesToHexStringTest()
    {
        hexTest();
    }

    @Test
    public void hexStringToBytesTest()
    {
        hexTest();
    }

    @Test
    public void hexTest()
    {
        String hexStr = "e19d05c5452598e24caad4a0d85a49146f7be089515c905ae6a19e8a578a6930";
        byte[] b = HexUtil.hexStringToBytes(hexStr);
        String h = HexUtil.bytesToHexString(b);
        assertTrue(hexStr.equals(h));


        String hexStr2 = "0bcdef34";
        byte[] bytes2 = new byte[]{(byte)0x0b, (byte)0xcd, (byte)0xef, (byte)0x34};
        assertTrue(hexStr2.equals(HexUtil.bytesToHexString(bytes2)));
        assertArrayEquals(bytes2,HexUtil.hexStringToBytes(hexStr2));
        assertTrue(hexStr2.equals(HexUtil.bytesToHexString(HexUtil.hexStringToBytes(hexStr2))));


        Random random = new Random();
        for (int j = 0; j < 1000; j++) {
            byte[] test = new byte[j];
            random.nextBytes(test);
            assert Arrays.equals(test, HexUtil.hexStringToBytes(HexUtil.bytesToHexString(test)));

            //十六进制字符串的长度是字节个数的2倍
            assertEquals(test.length*2, HexUtil.bytesToHexString(test).length());

            //只包含0123456789abcdef这些字符
            assert Pattern.matches("^[0123456789abcdef]*$", HexUtil.bytesToHexString(test));
        }
    }
}
