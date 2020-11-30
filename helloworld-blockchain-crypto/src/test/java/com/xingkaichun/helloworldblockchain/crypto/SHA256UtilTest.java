package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;


public class SHA256UtilTest {

    private static final Random RANDOM = new Random();

    private static final String[] messages =
            {
                    "",
                    "a",
                    "abc",
                    "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
            };

    private static final String[] digests =
            {
                    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                    "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb",
                    "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                    "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"
            };

    @Test
    public void digestTest()
    {
        for(int i=0;i<messages.length;i++){
            byte[] messageDigest = SHA256Util.digest(toByteArray(messages[i]));
            assertArrayEquals(Hex.decode(digests[i]), messageDigest);
        }
    }

    @Test
    public void digestTwice()
    {
        for (int j = 0; j < 100; j++) {
            byte[] randomBytes = new byte[j];
            RANDOM.nextBytes(randomBytes);
            assertArrayEquals(SHA256Util.digest(SHA256Util.digest(randomBytes)), SHA256Util.digestTwice(randomBytes));
        }
    }


    private byte[] toByteArray(String input)
    {
        byte[] bytes = new byte[input.length()];
        for (int i = 0; i != bytes.length; i++)
        {
            bytes[i] = (byte)input.charAt(i);
        }
        return bytes;
    }
}
