package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.account.Account;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 账户工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class AccountUtil {

    private static final ECDomainParameters ecParams;
    private static final SecureRandom secureRandom;
    private static final boolean compressed = false;

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        secureRandom = new SecureRandom();
    }

    /**
     * 随机生成一个账户
     */
    public static Account randomStringAccount() {
        try {
            ECKeyPairGenerator generator = new ECKeyPairGenerator();
            ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(ecParams, secureRandom);
            generator.init(keygenParams);
            AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
            ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate();
            ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();
            BigInteger priv = privParams.getD();
            byte[] pub = pubParams.getQ().getEncoded(compressed);
            String privateKey = encodePrivateKey(priv);
            String publicKey = encodePublicKey(pub);
            String address = addressFromPublicKey(publicKey);
            Account account = new Account(privateKey,publicKey,address);
            return account;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 私钥生成账户
     */
    public static Account stringAccountFrom(String privateKey) {
        try {
            BigInteger priv = privateKeyFrom(privateKey);
            byte[] ecPublicKey = publicFromPrivate(priv);

            String publicKey = encodePublicKey(ecPublicKey);
            String address = addressFromPublicKey(publicKey);
            Account account = new Account(privateKey,publicKey,address);
            return account;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥生成地址
     */
    public static String addressFromPublicKey(String publicKey) {
        try {
            byte[] bytePublicKey = HexUtil.hexStringToBytes(publicKey);
            return base58AddressFrom(bytePublicKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 签名
     */
    public static String signature(String privateKey, String rawData) {
       try {
           BigInteger bigIntegerPrivateKey = privateKeyFrom(privateKey);
           byte[] bytesSignature = signature(bigIntegerPrivateKey, ByteUtil.stringToBytes(rawData));
           String signature = Base64.getEncoder().encodeToString(bytesSignature);
           return signature;
       } catch (Exception e) {
            throw new RuntimeException(e);
       }
    }

    /**
     * 验证签名
     */
    public static boolean verifySignature(String publicKey, String rawData, String signature) {
        try {
            byte[] bytePublicKey = publicKeyFrom(publicKey);
            byte[] bytesSignature = Base64.getDecoder().decode(signature);
            return verifySignature(bytePublicKey,ByteUtil.stringToBytes(rawData),bytesSignature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * 由原始私钥推导出原始公钥
     */
    private static byte[] publicFromPrivate(BigInteger bigIntegerPrivateKey) {
        byte[] bytePublicKey = ecParams.getG().multiply(bigIntegerPrivateKey).getEncoded(compressed);
        return bytePublicKey;
    }
    /**
     * 由编码私钥解码出原始私钥
     */
    private static BigInteger privateKeyFrom(String privateKey) {
        BigInteger bigIntegerPrivateKey = new BigInteger(privateKey,16);
        return bigIntegerPrivateKey;
    }
    /**
     * 由编码公钥解码出原始公钥
     */
    private static byte[] publicKeyFrom(String publicKey) {
        byte[] bytePublicKey = HexUtil.hexStringToBytes(publicKey);
        return bytePublicKey;
    }
    /**
     * 将原始私钥进行编码操作，生成编码私钥
     */
    private static String encodePrivateKey(BigInteger bigIntegerPrivateKey) {
        String hexPrivateKey = HexUtil.bytesToHexString(bigIntegerPrivateKey.toByteArray());
        return hexPrivateKey;
    }
    /**
     * 将原始公钥进行编码操作，生成编码公钥
     */
    private static String encodePublicKey(byte[] bytePublicKey) {
        String hexPublicKey = HexUtil.bytesToHexString(bytePublicKey);
        return hexPublicKey;
    }

    /**
     * 签名
     */
    private static byte[] signature(BigInteger bigIntegerPrivateKey, byte[] input) {
        ECDSASigner signer = new ECDSASigner();
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bigIntegerPrivateKey, ecParams);
        signer.init(true, ecPrivateKeyParameters);
        BigInteger[] sigs = signer.generateSignature(input);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new ASN1Integer(sigs[0]));
            seq.addObject(new ASN1Integer(sigs[1]));
            seq.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }

    /**
     * 验证签名
     */
    private static boolean verifySignature(byte[] pub, byte[] rawData, byte[] signature) {
        ECDSASigner signer = new ECDSASigner();
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(ecParams.getCurve().decodePoint(pub), ecParams);
        signer.init(false, ecPublicKeyParameters);
        try {
            ASN1InputStream decoder = new ASN1InputStream(signature);
            DLSequence seq = (DLSequence) decoder.readObject();
            ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
            ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
            decoder.close();
            return signer.verifySignature(rawData, r.getValue(), s.getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥生成base58格式地址
     */
    private static String base58AddressFrom(byte[] bytePublicKey) {
        byte[] pubKSha256Digest = SHA256Util.digest(bytePublicKey);
        byte[] pubKSha256DigestRipeMD160Digest = RipeMD160Util.digest(pubKSha256Digest);

        //地址数组
        byte[] byteAddress = new byte[1 + 20 + 4];

        //将地址的版本号0x00存储进地址数组
        byteAddress[0] = 0x00;
        System.arraycopy(pubKSha256DigestRipeMD160Digest, 0, byteAddress, 1, 20);

        //计算公钥Hash
        byte[] versionAndPubKSha256RipeMD160 = new byte[21];
        System.arraycopy(byteAddress, 0, versionAndPubKSha256RipeMD160, 0, 21);
        byte[] doubleSHA256 = SHA256Util.digest(SHA256Util.digest(versionAndPubKSha256RipeMD160));

        //取前四位作为地址校验码，将校验码前四位加到地址数组的末四位
        System.arraycopy(doubleSHA256, 0, byteAddress, 21, 4);

        //用Base58编码地址数组
        String base58Address = Base58Util.encode(byteAddress);
        return base58Address;
    }
}
