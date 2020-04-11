package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSequenceGenerator;
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

public class KeyUtil {

    private static final ECDomainParameters ecParams;
    private static final SecureRandom secureRandom;

    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(),  params.getH());
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
        byte[] pub = pubParams.getQ().getEncoded();

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
        byte[] pubK = HexUtil.hexStringToBytes(stringPublicKey.getValue());
        return new StringAddress(base58AddressFrom(pubK));
    }

    /**
     * ECDSA签名
     */
    public static String applyECDSASig(StringPrivateKey stringPrivateKey, String data) {
       try {
           BigInteger priv = privateKeyFrom(stringPrivateKey);
           byte[] bytesSignature = applyECDSASig(data.getBytes(),priv);
           String strSignature = Base64.getEncoder().encodeToString(bytesSignature);
           return strSignature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECDSA签名验证
     */
    public static boolean verifyECDSASig(StringPublicKey stringPublicKey, String data, String strSignature) {
        try {
            byte[] bytePublicKey = publicKeyFrom(stringPublicKey);
            byte[] bytesSignature = Base64.getDecoder().decode(strSignature);
            return verifyECDSASig(bytePublicKey,data.getBytes(),bytesSignature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isEquals(StringPublicKey stringPublicKey, StringAddress stringAddress) {
        try {
            StringAddress tempStringAddress = stringAddressFrom(stringPublicKey);
            return isEquals(stringAddress,tempStringAddress);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEquals(StringAddress stringAddress1, StringAddress stringAddress2) {
        try {
            return stringAddress1.getValue().equals(stringAddress2.getValue());
        } catch (Exception e) {
            return false;
        }
    }













    private static byte[] publicFromPrivate(BigInteger bigIntegerPrivateKey) {
        byte[] bytePublicKey = ecParams.getG().multiply(bigIntegerPrivateKey).getEncoded();
        return bytePublicKey;
    }

    private static BigInteger privateKeyFrom(StringPrivateKey stringPrivateKey) {
        BigInteger bigIntegerPrivateKey = new BigInteger(stringPrivateKey.getValue(),16);
        return bigIntegerPrivateKey;
    }

    private static byte[] publicKeyFrom(StringPublicKey stringPublicKey) {
        byte[] bytePublicKey = HexUtil.hexStringToBytes(stringPublicKey.getValue());
        return bytePublicKey;
    }

    private static StringPrivateKey stringPrivateKeyFrom(BigInteger bigIntegerPrivateKey) {
        String hexPrivateKey = HexUtil.bytesToHexString(bigIntegerPrivateKey.toByteArray());
        return new StringPrivateKey(hexPrivateKey);
    }

    private static StringPublicKey stringPublicKeyFrom(byte[] bytePublicKey) {
        String hexPublicKey = HexUtil.bytesToHexString(bytePublicKey);
        return new StringPublicKey(hexPublicKey);
    }

    /**
     * ECDSA签名
     */
    private static byte[] applyECDSASig(byte[] input,BigInteger bigIntegerPrivateKey) {
        ECDSASigner signer = new ECDSASigner();
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bigIntegerPrivateKey, ecParams);
        signer.init(true, ecPrivateKeyParameters);
        BigInteger[] sigs = signer.generateSignature(input);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new DERInteger(sigs[0]));
            seq.addObject(new DERInteger(sigs[1]));
            seq.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }

    /**
     * ECDSA签名验证
     */
    private static boolean verifyECDSASig(byte[] data, byte[] signature, byte[] pub) {
        ECDSASigner signer = new ECDSASigner();
        ECPublicKeyParameters params = new ECPublicKeyParameters(ecParams.getCurve().decodePoint(pub), ecParams);
        signer.init(false, params);
        try {
            ASN1InputStream decoder = new ASN1InputStream(signature);
            DERSequence seq = (DERSequence) decoder.readObject();
            DERInteger r = (DERInteger) seq.getObjectAt(0);
            DERInteger s = (DERInteger) seq.getObjectAt(1);
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

        //地址
        byte[] addressBytes = new byte[1 + 20 + 4];

        //将地址的版本号0x00加到地址最前方
        addressBytes[0] = 0x00;
        System.arraycopy(pubKSha256RipeMD160, 0, addressBytes, 1, 20);

        //计算公钥Hash
        byte[] check1 = new byte[21];
        System.arraycopy(addressBytes, 0, check1, 0, 21);
        byte[] doubleSHA256 = SHA256Util.applySha256(SHA256Util.applySha256(check1));

        //取公钥hash的前四位作为地址校验码，将校验码前四位加到地址的末四位
        System.arraycopy(doubleSHA256, 0, addressBytes, 21, 4);

        //Base58编码
        String base58Address = Base58Util.encode(addressBytes);
        return base58Address;
    }
}
