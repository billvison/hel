package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
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
 * 秘钥工具类
 */
public class KeyUtil {

    private static final ECDomainParameters ecParams;
    private static final SecureRandom secureRandom;

    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        secureRandom = new SecureRandom();
    }

    /**
     * 随机生成一个秘钥
     */
    public static StringKey randomStringKey() throws Exception {
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(ecParams, secureRandom);
        generator.init(keygenParams);
        AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate();
        ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();
        BigInteger priv = privParams.getD();
        // The public key is an encoded point on the elliptic curve. It has no meaning independent of the curve.
        byte[] pub = pubParams.getQ().getEncoded(false);
        StringKey stringKey = new StringKey();
        StringPrivateKey stringPrivateKey = stringPrivateKeyFrom(priv);
        StringPublicKey stringPublicKey = stringPublicKeyFrom(pub);
        StringAddress stringAddress = stringAddressFrom(stringPublicKey);
        stringKey.setStringPrivateKey(stringPrivateKey);
        stringKey.setStringPublicKey(stringPublicKey);
        stringKey.setStringAddress(stringAddress);
        return stringKey;
    }

    /**
     * 私钥生成秘钥
     */
    public static StringKey stringKeyFrom(StringPrivateKey stringPrivateKey) throws Exception {
        BigInteger priv = privateKeyFrom(stringPrivateKey);
        byte[] ecPublicKey = publicFromPrivate(priv);
        StringKey stringKey = new StringKey();

        StringPublicKey stringPublicKey = stringPublicKeyFrom(ecPublicKey);
        StringAddress stringAddress = stringAddressFrom(stringPublicKey);
        stringKey.setStringPrivateKey(stringPrivateKey);
        stringKey.setStringPublicKey(stringPublicKey);
        stringKey.setStringAddress(stringAddress);
        return stringKey;
    }

    /**
     * 公钥生成地址
     */
    public static StringAddress stringAddressFrom(StringPublicKey stringPublicKey) throws Exception {
        byte[] bytePublicKey = HexUtil.hexStringToBytes(stringPublicKey.getValue());
        return new StringAddress(base58AddressFrom(bytePublicKey));
    }

    /**
     * 签名
     */
    public static String signature(StringPrivateKey stringPrivateKey, String data) {
       try {
           BigInteger bigIntegerPrivateKey = privateKeyFrom(stringPrivateKey);
           byte[] bytesSignature = signature(bigIntegerPrivateKey,data.getBytes());
           String stringSignature = Base64.getEncoder().encodeToString(bytesSignature);
           return stringSignature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证签名
     */
    public static boolean verifySignature(StringPublicKey stringPublicKey, String data, String stringSignature) {
        try {
            byte[] bytePublicKey = publicKeyFrom(stringPublicKey);
            byte[] bytesSignature = Base64.getDecoder().decode(stringSignature);
            return verifySignature(bytePublicKey,data.getBytes(),bytesSignature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断公钥与地址是否等价
     */
    public static boolean isEquals(StringPublicKey stringPublicKey, StringAddress stringAddress) {
        try {
            StringAddress tempStringAddress = stringAddressFrom(stringPublicKey);
            return isEquals(stringAddress,tempStringAddress);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断地址是否等价
     */
    public static boolean isEquals(StringAddress stringAddress1, StringAddress stringAddress2) {
        try {
            return stringAddress1.getValue().equals(stringAddress2.getValue());
        } catch (Exception e) {
            return false;
        }
    }




    /**
     * 由原始私钥推导出原始公钥
     */
    private static byte[] publicFromPrivate(BigInteger bigIntegerPrivateKey) {
        byte[] bytePublicKey = ecParams.getG().multiply(bigIntegerPrivateKey).getEncoded();
        return bytePublicKey;
    }
    /**
     * 由编码私钥解码出原始私钥
     */
    private static BigInteger privateKeyFrom(StringPrivateKey stringPrivateKey) {
        BigInteger bigIntegerPrivateKey = new BigInteger(stringPrivateKey.getValue(),16);
        return bigIntegerPrivateKey;
    }
    /**
     * 由编码公钥解码出原始公钥
     */
    private static byte[] publicKeyFrom(StringPublicKey stringPublicKey) {
        byte[] bytePublicKey = HexUtil.hexStringToBytes(stringPublicKey.getValue());
        return bytePublicKey;
    }
    /**
     * 将原始私钥进行编码操作，生成编码私钥
     */
    private static StringPrivateKey stringPrivateKeyFrom(BigInteger bigIntegerPrivateKey) {
        String hexPrivateKey = HexUtil.bytesToHexString(bigIntegerPrivateKey.toByteArray());
        return new StringPrivateKey(hexPrivateKey);
    }
    /**
     * 将原始公钥进行编码操作，生成编码公钥
     */
    private static StringPublicKey stringPublicKeyFrom(byte[] bytePublicKey) {
        String hexPublicKey = HexUtil.bytesToHexString(bytePublicKey);
        return new StringPublicKey(hexPublicKey);
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
    private static boolean verifySignature(byte[] pub, byte[] data, byte[] signature) {
        ECDSASigner signer = new ECDSASigner();
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(ecParams.getCurve().decodePoint(pub), ecParams);
        signer.init(false, ecPublicKeyParameters);
        try {
            ASN1InputStream decoder = new ASN1InputStream(signature);
            DLSequence seq = (DLSequence) decoder.readObject();
            ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
            ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
            decoder.close();
            return signer.verifySignature(data, r.getValue(), s.getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥生成base58格式地址
     */
    private static String base58AddressFrom(byte[] bytePublicKey) throws Exception {
        byte[] pubKSha256 = SHA256Util.applySha256(bytePublicKey);
        byte[] pubKSha256RipeMD160 = RipeMD160Util.ripeMD160(pubKSha256);

        //地址数组
        byte[] byteAddress = new byte[1 + 20 + 4];

        //将地址的版本号0x00存储进地址数组
        byteAddress[0] = 0x00;
        System.arraycopy(pubKSha256RipeMD160, 0, byteAddress, 1, 20);

        //计算公钥Hash
        byte[] versionAndPubKSha256RipeMD160 = new byte[21];
        System.arraycopy(byteAddress, 0, versionAndPubKSha256RipeMD160, 0, 21);
        byte[] doubleSHA256 = SHA256Util.applySha256(SHA256Util.applySha256(versionAndPubKSha256RipeMD160));

        //取前四位作为地址校验码，将校验码前四位加到地址数组的末四位
        System.arraycopy(doubleSHA256, 0, byteAddress, 21, 4);

        //用Base58编码地址数组
        String base58Address = Base58Util.encode(byteAddress);
        return base58Address;
    }
}
