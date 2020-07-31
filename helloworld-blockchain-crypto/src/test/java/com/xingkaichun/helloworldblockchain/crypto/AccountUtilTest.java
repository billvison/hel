package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccountUtilTest extends AccountUtil{

    private final static Account ACCOUNT = new Account("25e25210dce702d4e36b6c8a17e18dc1d02a9e4f0d1d31c4aee77327cf1641cc"
            ,"043f099e71ac2b0ca6ca72b4e00539f6972a5f2769bdbfb7b357691c00815bb33860518bb1a1e047a652fee2a21464b95d8176bdbf66f8f4a07ccad52c74321772"
            ,"164qdFjYmbwPybeXrfFayAgjpp1nsCuWRg"
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
        assertTrue(account.getPrivateKey().equals(bitcoinjECKey.getPrivateKeyAsHex()));
        assertTrue(account.getPublicKey().equals(bitcoinjECKey.getPublicKeyAsHex()));
        Address bitcoinjAddress = Address.fromKey(NetworkParameters.fromID(NetworkParameters.ID_MAINNET),bitcoinjECKey, Script.ScriptType.P2PKH);
        assertTrue(account.getAddress().equals(bitcoinjAddress.toString()));
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
        assertTrue(ACCOUNT.getPrivateKey().equals(bitcoinjECKey.getPrivateKeyAsHex()));
        assertTrue(ACCOUNT.getPublicKey().equals(bitcoinjECKey.getPublicKeyAsHex()));
        Address bitcoinjAddress = Address.fromKey(NetworkParameters.fromID(NetworkParameters.ID_MAINNET),bitcoinjECKey, Script.ScriptType.P2PKH);
        assertTrue(ACCOUNT.getAddress().equals(bitcoinjAddress.toString()));
    }
}