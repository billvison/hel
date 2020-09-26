package com.xingkaichun.helloworldblockchain.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Base58UtilTest {


    @Test
    public void encodeTest()
    {
        byte[] testbytes = "Hello World".getBytes();
        assertEquals("JxF12TrwUP45BMd", Base58Util.encode(testbytes));

        BigInteger bi = BigInteger.valueOf(3471844090L);
        assertEquals("16Ho7Hs", Base58Util.encode(bi.toByteArray()));

        byte[] zeroBytes1 = new byte[1];
        assertEquals("1", Base58Util.encode(zeroBytes1));

        byte[] zeroBytes7 = new byte[7];
        assertEquals("1111111", Base58Util.encode(zeroBytes7));

        // test empty encode
        assertEquals("", Base58Util.encode(new byte[0]));
    }

    @Test
    public void decodeTest()
    {
        byte[] testbytes = "Hello World".getBytes();
        byte[] actualbytes = Base58Util.decode("JxF12TrwUP45BMd");
        assertTrue(new String(actualbytes), Arrays.equals(testbytes, actualbytes));

        assertTrue("1", Arrays.equals(Base58Util.decode("1"), new byte[1]));
        assertTrue("1111", Arrays.equals(Base58Util.decode("1111"), new byte[4]));

        // Test decode of empty String.
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
