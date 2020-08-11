package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.script.Script;
import org.junit.Test;

import java.util.Random;

import static org.bitcoinj.core.Utils.HEX;
import static org.junit.Assert.assertArrayEquals;
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
        assertAccount(account);
    }

    @Test
    public void accountFromPrivateKeyTest()
    {
        Account account = AccountUtil.accountFromPrivateKey(ACCOUNT_1.getPrivateKey());
        assertTrue(ACCOUNT_1.getPrivateKey().equals(account.getPrivateKey()));
        assertTrue(ACCOUNT_1.getPublicKey().equals(account.getPublicKey()));
        assertTrue(ACCOUNT_1.getAddress().equals(account.getAddress()));
        assertAccount(account);
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
        verifySignatureTest();
    }

    //来源org.bitcoinj.core.ECKeyTest.testSignatures()
    @Test
    public void verifySignatureTest()
    {
        byte[] zeroValueByte32Length = new byte[32];
        Account account = AccountUtil.accountFromPrivateKey("180cb41c7c600be951b5d3d0a7334acc7506173875834f7a6c4c786a28fcbb19");
        byte[] signatureByAccount = AccountUtil.signature(AccountUtil.privateKeyFrom(account.getPrivateKey()),zeroValueByte32Length);
        boolean verifySignatureByAccount = AccountUtil.verifySignature(AccountUtil.publicKeyFrom(account.getPublicKey()),zeroValueByte32Length,signatureByAccount);
        assertTrue(verifySignatureByAccount);

        byte[] signatureByFixValue = HEX.decode("3046022100dffbc26774fc841bbe1c1362fd643609c6e42dcb274763476d87af2c0597e89e022100c59e3c13b96b316cae9fa0ab0260612c7a133a6fe2b3445b6bf80b3123bf274d");
        boolean verifySignatureByFixValue = AccountUtil.verifySignature(AccountUtil.publicKeyFrom(account.getPublicKey()),zeroValueByte32Length,signatureByFixValue);
        assertTrue(verifySignatureByFixValue);
    }

    private void randomAssertSignature() throws Exception{
        Account account = AccountUtil.randomAccount();
        ECKey bitcoinjECKey = ECKey.fromPrivate(AccountUtil.privateKeyFrom(account.getPrivateKey()),false);

        //随机生成32位数组字节
        byte[] randomByte = new byte[32];
        new Random().nextBytes(randomByte);
        Sha256Hash randomSha256Hash = Sha256Hash.wrap(randomByte);

        byte[] signByAccount = AccountUtil.signature(AccountUtil.privateKeyFrom(account.getPrivateKey()),randomByte);
        byte[] signByAccount2 = AccountUtil.signature(AccountUtil.privateKeyFrom(account.getPrivateKey()),randomByte);
        //校验同一私钥，两次签名是否一致
        assertArrayEquals(signByAccount,signByAccount2);
        //校验签名是否正确
        assertTrue(AccountUtil.verifySignature(AccountUtil.publicKeyFrom(account.getPublicKey()),randomByte,signByAccount));
        //用bitcoinj校验签名是否正确
        assert(bitcoinjECKey.verify(randomSha256Hash.getBytes(), signByAccount));


        byte[] signByBitcoinj = bitcoinjECKey.sign(randomSha256Hash).encodeToDER();
        ECKey bitcoinjECKey2 = ECKey.fromPrivate(AccountUtil.privateKeyFrom(account.getPrivateKey()),false);
        byte[] signByBitcoinj2 = bitcoinjECKey2.sign(randomSha256Hash).encodeToDER();
        //校验同一私钥，两次bitcoinj签名是否一致
        assertArrayEquals(signByBitcoinj,signByBitcoinj2);
        //bitcoinj校验bitcoinj签名是否正确
        assert(bitcoinjECKey.verify(randomSha256Hash.getBytes(), signByBitcoinj));
        //校验bitcoinj签名是否正确
        assertTrue(AccountUtil.verifySignature(AccountUtil.publicKeyFrom(account.getPublicKey()),randomByte,signByBitcoinj));

        //assertArrayEquals(signByAccount,signByBitcoinj);
    }

    @Test
    public void allTest() throws Exception
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

        for(int i=0;i<1000;i++){
            randomAssertSignature();
        }
    }


    private static void assertAccount(Account account){
        Account accountFromPrivateKey = AccountUtil.accountFromPrivateKey(account.getPrivateKey());
        assertTrue(account.getPrivateKey().equals(accountFromPrivateKey.getPrivateKey()));
        assertTrue(account.getPublicKey().equals(accountFromPrivateKey.getPublicKey()));
        assertTrue(account.getAddress().equals(accountFromPrivateKey.getAddress()));

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