package com.xingkaichun.helloworldblockchain.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

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

}
