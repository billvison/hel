package com.xingkaichun.helloworldblockchain.crypto;

import com.google.common.primitives.Longs;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;


public class ByteUtilTest {

    @Test
    public void long64ToBytes64WithBigEndianTest()
    {
        for(int i=0;i<10000;i++){
            Random random = new Random();
            long number = random.nextLong();
            long resumeNumber = ByteUtil.bytes64ToLong64WithBigEndian(ByteUtil.long64ToBytes64WithBigEndian(number));
            //校验互转
            Assert.assertEquals(number,resumeNumber);
            //用google guava校验long转8字节大端数组
            Assert.assertArrayEquals(Longs.toByteArray(number),ByteUtil.long64ToBytes64WithBigEndian(number));
        }

    }

    @Test
    public void bytes64ToLong64WithBigEndianTest()
    {
        Random random = new Random();
        byte[] byte8 = new byte[8];
        for(int i=0;i<10000;i++){
            random.nextBytes(byte8);
            byte[] resumeByte8 = ByteUtil.long64ToBytes64WithBigEndian(ByteUtil.bytes64ToLong64WithBigEndian(byte8));
            //校验互转
            Assert.assertArrayEquals(byte8,resumeByte8);
            //用google guava校验8字节大端数组转long
            Assert.assertEquals(Longs.fromByteArray(byte8),ByteUtil.bytes64ToLong64WithBigEndian(resumeByte8));
        }
    }
}
