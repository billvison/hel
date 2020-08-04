package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class AccountUtilTest extends AccountUtil{

    private final static Account ACCOUNT_1 = new Account("d5c654eeff2cf1c6af16d721f31854ef447dfc61a6344bf1ba7108ec77d29b80"
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

        assertAccount(account);
    }

    @Test
    public void accountFromPrivateKeyTest()
    {
        Account account = AccountUtil.accountFromPrivateKey(ACCOUNT_1.getPrivateKey());
        assertTrue(ACCOUNT_1.getPrivateKey().equals(account.getPrivateKey()));
        assertTrue(ACCOUNT_1.getPublicKey().equals(account.getPublicKey()));
        assertTrue(ACCOUNT_1.getAddress().equals(account.getAddress()));
    }

    @Test
    public void addressFromPublicKeyTest()
    {
        String address = AccountUtil.addressFromPublicKey(ACCOUNT_1.getPublicKey());
        assertTrue(ACCOUNT_1.getAddress().equals(address));
    }

    @Test
    public void signatureTest()
    {
        //TODO 用一个写死的数据验证
        //遍历随机生成验证
        String rawData = "123456";
        String signature = AccountUtil.signature(ACCOUNT_1.getPrivateKey(),rawData);
        boolean verifySignature = AccountUtil.verifySignature(ACCOUNT_1.getPublicKey(),rawData,signature);
        assertTrue(verifySignature);
    }

    @Test
    public void verifySignatureTest()
    {
        //TODO 用一个写死的数据验证
        //遍历随机生成验证
        String rawData = "123456";
        String signature = AccountUtil.signature(ACCOUNT_1.getPrivateKey(),rawData);
        boolean verifySignature = AccountUtil.verifySignature(ACCOUNT_1.getPublicKey(),rawData,signature);
        assertTrue(verifySignature);
    }

    @Test
    public void allTest()
    {
        assertAccount(ACCOUNT_1);

        for (int i=0;i<1000;i++){
            Account account = AccountUtil.randomAccount();
            assertAccount(account);
        }

        for (int i=0;i<1000;i++){
            Account account = AccountUtil.accountFromPrivateKey(randomPrivateKey());
            assertAccount(account);
        }
    }


    private static void assertAccount(Account account){
        //用bitcoinj解析账户，并对比私钥、公钥、地址
        ECKey bitcoinjECKey = ECKey.fromPrivate(AccountUtil.privateKeyFrom(account.getPrivateKey()),false);
        assertTrue(account.getPrivateKey().equals(bitcoinjECKey.getPrivateKeyAsHex()));
        assertTrue(account.getPublicKey().equals(bitcoinjECKey.getPublicKeyAsHex()));
        Address bitcoinjAddress = Address.fromKey(NetworkParameters.fromID(NetworkParameters.ID_MAINNET),bitcoinjECKey, Script.ScriptType.P2PKH);
        assertTrue(account.getAddress().equals(bitcoinjAddress.toString()));
    }


    private static String randomPrivateKey() {
        StringBuffer result = new StringBuffer();
        int length = new Random().nextInt(63)+2;
        for(int i=0;i<length;i++) {
            result.append(Integer.toHexString(new Random().nextInt(16)));
        }
        return result.toString();
    }
}