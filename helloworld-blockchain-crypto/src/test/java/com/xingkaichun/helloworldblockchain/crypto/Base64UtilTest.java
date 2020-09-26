package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class Base64UtilTest {

    private static final String sample1 = "mO4TyLWG7vjFWdKT8IJcVbZ/jwc=";
    private static final byte[] sample1Bytes = Hex.decode("98ee13c8b586eef8c559d293f0825c55b67f8f07");
    private static final String sample2 = "F4I4p8Vf/mS+Kxvri3FPoMcqmJ1f";
    private static final byte[] sample2Bytes = Hex.decode("178238a7c55ffe64be2b1beb8b714fa0c72a989d5f");
    private static final String sample3 = "UJmEdJYodqHJmd7Rtv6/OP29/jUEFw==";
    private static final byte[] sample3Bytes = Hex.decode("50998474962876a1c999ded1b6febf38fdbdfe350417");

    private static final String invalid1 = "%O4TyLWG7vjFWdKT8IJcVbZ/jwc=";
    private static final String invalid2 = "F%I4p8Vf/mS+Kxvri3FPoMcqmJ1f";
    private static final String invalid3 = "UJ%EdJYodqHJmd7Rtv6/OP29/jUEFw==";
    private static final String invalid4 = "mO4%yLWG7vjFWdKT8IJcVbZ/jwc=";
    private static final String invalid5 = "UJmEdJYodqHJmd7Rtv6/OP29/jUEF%==";
    private static final String invalid6 = "mO4TyLWG7vjFWdKT8IJcVbZ/jw%=";
    private static final String invalid7 = "F4I4p8Vf/mS+Kxvri3FPoMcqmJ1%";
    private static final String invalid8 = "UJmEdJYodqHJmd7Rtv6/OP29/jUE%c==";
    private static final String invalid9 = "mO4TyLWG7vjFWdKT8IJcVbZ/j%c=";
    private static final String invalida = "F4I4p8Vf/mS+Kxvri3FPoMcqmJ%1";
    private static final String invalidb = "UJmEdJYodqHJmd7Rtv6/OP29/jU%Fc==";
    private static final String invalidc = "mO4TyLWG7vjFWdKT8IJcVbZ/%wc=";
    private static final String invalidd = "F4I4p8Vf/mS+Kxvri3FPoMcqm%1c";

    @Test
    public void encodeTest()
    {
        base64Test();
    }

    @Test
    public void decodeTest()
    {
        base64Test();
    }

    @Test
    public void base64Test()
    {
        assertArrayEquals(sample1Bytes, Base64Util.decode(sample1));
        assertEquals(sample1, Base64Util.encode(sample1Bytes));
        assertArrayEquals(sample1Bytes, Base64Util.decode(Base64Util.encode(sample1Bytes)));
        assertEquals(sample1, Base64Util.encode(Base64Util.decode(sample1)));

        assertArrayEquals(sample2Bytes, Base64Util.decode(sample2));
        assertEquals(sample2, Base64Util.encode(sample2Bytes));
        assertArrayEquals(sample2Bytes, Base64Util.decode(Base64Util.encode(sample2Bytes)));
        assertEquals(sample2, Base64Util.encode(Base64Util.decode(sample2)));

        assertArrayEquals(sample3Bytes, Base64Util.decode(sample3));
        assertEquals(sample3, Base64Util.encode(sample3Bytes));
        assertArrayEquals(sample3Bytes, Base64Util.decode(Base64Util.encode(sample3Bytes)));
        assertEquals(sample3, Base64Util.encode(Base64Util.decode(sample3)));

        Random random = new Random();
        for (int j = 1; j < 1000; j++) {
            byte[] test = new byte[j];
            random.nextBytes(test);
            assert Arrays.equals(test, Base64Util.decode(Base64Util.encode(test)));

            //前面字符只能包含'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/',末尾可以有0到2个'='(等于号)作为填充。
            assert Pattern.matches("^[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/]*[=]{0,2}$", Base64Util.encode(test));
        }
    }

    @Test
    public void invalidTest()
    {
        String[] invalid = new String[] { invalid1, invalid2, invalid3, invalid4, invalid5, invalid6, invalid7, invalid8, invalid9, invalida, invalidb, invalidc, invalidd };

        for (int i = 0; i != invalid.length; i++)
        {
            invalidTest(invalid[i]);
        }
    }

    private void invalidTest(String data)
    {
        try
        {
            Base64Util.decode(data);
        }
        catch (DecoderException e)
        {
            return;
        }
        fail("invalid String data parsed");
    }
}
