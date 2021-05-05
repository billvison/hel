package com.xingkaichun.helloworldblockchain.crypto;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;


public class NumberUtilTest {

    @Test
    public void long64ToBytes64WithBigEndianTest()
    {
        for(int i=0;i<10000;i++){
            Random random = new Random();
            long number = random.nextLong();
            long resumeNumber = NumberUtil.byte8ToLong8(NumberUtil.long8ToByte8(number));
            //校验互转
            Assert.assertEquals(number,resumeNumber);
            //校验long转8字节大端数组
            Assert.assertArrayEquals(long64ToBytes64WithBigEndian(number),NumberUtil.long8ToByte8(number));
        }

    }

    @Test
    public void bytes64ToLong64WithBigEndianTest()
    {
        Random random = new Random();
        byte[] byte8 = new byte[8];
        for(int i=0;i<10000;i++){
            random.nextBytes(byte8);
            byte[] resumeByte8 = NumberUtil.long8ToByte8(NumberUtil.byte8ToLong8(byte8));
            //校验互转
            Assert.assertArrayEquals(byte8,resumeByte8);
            //校验8字节大端数组转long
            Assert.assertEquals(bytes64ToLong64WithBigEndian(byte8),NumberUtil.byte8ToLong8(resumeByte8));
        }
    }

    /**
     * long转换为(大端模式)8个字节的字节数组(8*8=64个bit)。
     */
    private static byte[] long64ToBytes64WithBigEndian(long value) {
        byte[] bytes = new byte[8];
        bytes[7] = (byte)(0xFF & (value));
        bytes[6] = (byte)(0xFF & (value >> 8));
        bytes[5] = (byte)(0xFF & (value >> 16));
        bytes[4] = (byte)(0xFF & (value >> 24));
        bytes[3] = (byte)(0xFF & (value >> 32));
        bytes[2] = (byte)(0xFF & (value >> 40));
        bytes[1] = (byte)(0xFF & (value >> 48));
        bytes[0] = (byte)(0xFF & (value >> 56));
        return bytes;
    }

    /**
     * (大端模式)8个字节的字节数组(8*8=64个bit)转换为long。
     */
    private static long bytes64ToLong64WithBigEndian(byte[] bytes) {
        long n0 = bytes[0] & 0xff;
        long n1 = bytes[1] & 0xff;
        long n2 = bytes[2] & 0xff;
        long n3 = bytes[3] & 0xff;
        long n4 = bytes[4] & 0xff;
        long n5 = bytes[5] & 0xff;
        long n6 = bytes[6] & 0xff;
        long n7 = bytes[7] & 0xff;

        n7 <<= 0;
        n6 <<= 8;
        n5 <<= 16;
        n4 <<= 24;
        n3 <<= 32;
        n2 <<= 40;
        n1 <<= 48;
        n0 <<= 56;
        long n = n0 | n1 | n2 | n3 | n4 | n5 | n6 | n7;
        return n;
    }
}
