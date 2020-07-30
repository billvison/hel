package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.account.Account;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccountUtilTest {

    private final static Account STRING_ACCOUNT = new Account("25e25210dce702d4e36b6c8a17e18dc1d02a9e4f0d1d31c4aee77327cf1641cc"
            ,"043f099e71ac2b0ca6ca72b4e00539f6972a5f2769bdbfb7b357691c00815bb33860518bb1a1e047a652fee2a21464b95d8176bdbf66f8f4a07ccad52c74321772"
            ,"164qdFjYmbwPybeXrfFayAgjpp1nsCuWRg");

    @Test
    public void stringAccountFromTest()
    {
        Account account = AccountUtil.stringAccountFrom(STRING_ACCOUNT.getPrivateKey());
        assertTrue(STRING_ACCOUNT.getPrivateKey().equals(account.getPrivateKey()));
        assertTrue(STRING_ACCOUNT.getAddress().equals(account.getAddress()));
        assertTrue(STRING_ACCOUNT.getAddress().equals(account.getAddress()));
    }

    @Test
    public void addressFromPublicKeyTest()
    {
        String address = AccountUtil.addressFromPublicKey(STRING_ACCOUNT.getPublicKey());
        assertTrue(STRING_ACCOUNT.getAddress().equals(address));
    }

}
