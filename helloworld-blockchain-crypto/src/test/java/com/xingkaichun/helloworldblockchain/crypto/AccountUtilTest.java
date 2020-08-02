package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class AccountUtilTest extends AccountUtil{

    private final static Account ACCOUNT = new Account("00d5c654eeff2cf1c6af16d721f31854ef447dfc61a6344bf1ba7108ec77d29b80"
            ,"044a99261b7a4b4bad2cac46defa41936b5807bf35dc5d7645d4976431666c741f3be030868718a20f875c69e9f1b781502429584f2c754c0aa8432719df1ed65e"
            ,"13e2eYT45FbRMBuyg7CHjZuEkjfMCD7xGg"
            );

    @Test
    public void randomAccountKeyTest()
    {
        Account account = AccountUtil.randomAccount();
        Account accountFromPrivateKey = AccountUtil.accountFromPrivateKey(account.getPrivateKey());
        assertTrue(account.getPrivateKey().equals(accountFromPrivateKey.getPrivateKey()));
        assertTrue(account.getPublicKey().equals(accountFromPrivateKey.getPublicKey()));
        assertTrue(account.getAddress().equals(accountFromPrivateKey.getAddress()));

        //用bitcoinj解析账户，并对比私钥、公钥、地址
        //
        /**
         * TODO 为什么有时失败 有时成功？？
         * 自己工具类的私钥有00开头，而bitcoinj十六进制前如果是00则删除
         */
        ECKey bitcoinjECKey = ECKey.fromPrivate(AccountUtil.privateKeyFrom(account.getPrivateKey()),false);
        assertAccount(account,bitcoinjECKey);
    }

    @Test
    public void accountFromPrivateKeyTest()
    {
        Account account = AccountUtil.accountFromPrivateKey(ACCOUNT.getPrivateKey());
        assertTrue(ACCOUNT.getPrivateKey().equals(account.getPrivateKey()));
        assertTrue(ACCOUNT.getPublicKey().equals(account.getPublicKey()));
        assertTrue(ACCOUNT.getAddress().equals(account.getAddress()));
    }

    @Test
    public void addressFromPublicKeyTest()
    {
        String address = AccountUtil.addressFromPublicKey(ACCOUNT.getPublicKey());
        assertTrue(ACCOUNT.getAddress().equals(address));
    }

    @Test
    public void signatureTest()
    {
        //TODO 用一个写死的数据验证
        //遍历随机生成验证
        String rawData = "123456";
        String signature = AccountUtil.signature(ACCOUNT.getPrivateKey(),rawData);
        boolean verifySignature = AccountUtil.verifySignature(ACCOUNT.getPublicKey(),rawData,signature);
        assertTrue(verifySignature);
    }

    @Test
    public void verifySignatureTest()
    {
        //TODO 用一个写死的数据验证
        //遍历随机生成验证
        String rawData = "123456";
        String signature = AccountUtil.signature(ACCOUNT.getPrivateKey(),rawData);
        boolean verifySignature = AccountUtil.verifySignature(ACCOUNT.getPublicKey(),rawData,signature);
        assertTrue(verifySignature);
    }

    @Test
    public void allTest()
    {
        //由私钥生成账户进行验证
        Account account = AccountUtil.accountFromPrivateKey(ACCOUNT.getPrivateKey());
        assertTrue(ACCOUNT.getPrivateKey().equals(account.getPrivateKey()));
        assertTrue(ACCOUNT.getPublicKey().equals(account.getPublicKey()));
        assertTrue(ACCOUNT.getAddress().equals(account.getAddress()));


        //用bitcoinj解析账户，并对比私钥、公钥、地址
        ECKey bitcoinjECKey = ECKey.fromPrivate(AccountUtil.privateKeyFrom(ACCOUNT.getPrivateKey()),false);
        assertAccount(ACCOUNT,bitcoinjECKey);


        for (int i=0;i<100;i++){
            account = AccountUtil.randomAccount();
            bitcoinjECKey = ECKey.fromPrivate(AccountUtil.privateKeyFrom(account.getPrivateKey()),false);
            assertAccount(account,bitcoinjECKey);
        }
    }


    public static void assertAccount(Account account,ECKey bitcoinjECKey){
        BigInteger accountPrivate = AccountUtil.privateKeyFrom(account.getPrivateKey());
        String accountPrivateHex = Utils.HEX.encode(accountPrivate.toByteArray());
        assertTrue(accountPrivate.equals(bitcoinjECKey.getPrivKey()));
        assertTrue(account.getPrivateKey().equals(accountPrivateHex));

        //accountPrivate.toByteArray() 有时候32字节 有时候33字节
        assertArrayEquals(accountPrivate.toByteArray(),Utils.bigIntegerToBytes(bitcoinjECKey.getPrivKey(), accountPrivate.toByteArray().length));

        //bitcoinj 私钥前缀如果是0xOO，则会丢弃
        String accountPrivate2Hex = Utils.HEX.encode(Utils.bigIntegerToBytes(bitcoinjECKey.getPrivKey(), accountPrivate.toByteArray().length));
        //assertTrue(accountPrivate.equals(accountPrivate2Hex));
        //assertTrue(account.getPrivateKey().contains(bitcoinjECKey.getPrivateKeyAsHex()));

        assertTrue(account.getPublicKey().equals(bitcoinjECKey.getPublicKeyAsHex()));

        Address bitcoinjAddress = Address.fromKey(NetworkParameters.fromID(NetworkParameters.ID_MAINNET),bitcoinjECKey, Script.ScriptType.P2PKH);
        assertTrue(account.getAddress().equals(bitcoinjAddress.toString()));
    }
}