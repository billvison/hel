package com.xingkaichun.helloworldblockchain.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class Base58UtilTest {


    @Test
    public void encodeTest()
    {
        assertEquals("JxF12TrwUP45BMd", Base58Util.encode("Hello World".getBytes()));

        BigInteger bi = BigInteger.valueOf(3471844090L);
        assertEquals("16Ho7Hs", Base58Util.encode(bi.toByteArray()));

        assertEquals("1", Base58Util.encode(new byte[1]));
        assertEquals("1111111", Base58Util.encode(new byte[7]));

        assertEquals("", Base58Util.encode(new byte[0]));
    }

    @Test
    public void decodeTest()
    {
        assertArrayEquals(new byte[1], Base58Util.decode("1"));
        assertArrayEquals(new byte[4], Base58Util.decode("1111"));

        assertArrayEquals("Hello World".getBytes(), Base58Util.decode("JxF12TrwUP45BMd"));
        assertEquals(0, Base58Util.decode("").length);
    }


    @Test
    public void base58Test()
    {
        Random random = new Random();
        for (int j = 1; j < 1000; j++) {
            byte[] test = new byte[j];
            random.nextBytes(test);
            assert Arrays.equals(test, Base58Util.decode(Base58Util.encode(test)));

            //只包含123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz这些字符
            assert Pattern.matches("^[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]*$", Base58Util.encode(test));
        }
    }

}
