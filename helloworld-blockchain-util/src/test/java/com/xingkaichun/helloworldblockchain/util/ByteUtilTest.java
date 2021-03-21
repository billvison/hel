package com.xingkaichun.helloworldblockchain.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;


public class ByteUtilTest {

    @Test
    public void test()
    {
        for(int i=0;i<10000;i++){
            Random random = new Random();
            long number = random.nextLong();
            System.out.println(number);
            long resumeNumber = ByteUtil.bytes8BigEndianToLong(ByteUtil.longToBytes8BigEndian(number));
            Assert.assertEquals(number,resumeNumber);
        }
    }
}
