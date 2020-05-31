package com.xingkaichun.helloworldblockchain;

import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;

import java.security.Security;

public class T {


    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        StringAccount stringAccount = AccountUtil.randomStringAccount();
        StringAccount stringAccount2 = AccountUtil.stringAccountFrom(stringAccount.getStringPrivateKey());
        System.out.println(stringAccount);
    }
}
