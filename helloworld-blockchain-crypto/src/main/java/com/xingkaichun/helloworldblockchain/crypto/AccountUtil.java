package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 账户工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class AccountUtil {

    private static final Logger logger = LoggerFactory.getLogger(AccountUtil.class);

    private static final ECDomainParameters CURVE;
    private static final SecureRandom SECURE_RANDOM;
    private static final boolean COMPRESSED = true;
    private static final BigInteger HALF_CURVE_ORDER;


    private static final byte VERSION = 0x00;

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        SECURE_RANDOM = new SecureRandom();
        HALF_CURVE_ORDER = CURVE.getN().shiftRight(1);
    }

    /**
     * 这里是为了解决交易延展性攻击。
     * Returns true if the S component is "low", that means it is below {@link ECKey#HALF_CURVE_ORDER}. See <a
     * href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">BIP62</a>.
     * 参考：bitcoinj-core-0.15.10.jar org.bitcoinj.core.ECKey.isCanonical()
     */
    private static boolean isCanonical(BigInteger s) {
        return s.compareTo(HALF_CURVE_ORDER) <= 0;
    }

    /**
     * 随机生成一个账户
     */
    public static Account randomAccount() {
        try {
            ECKeyPairGenerator generator = new ECKeyPairGenerator();
            ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(CURVE, SECURE_RANDOM);
            generator.init(keygenParams);
            AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
            ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters) keypair.getPrivate();
            ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();
            BigInteger bigIntegerPrivateKey = ecPrivateKeyParameters.getD();
            byte[] publicKey = pubParams.getQ().getEncoded(COMPRESSED);
            String stringPrivateKey = encodePrivateKey(bigIntegerPrivateKey);
            String stringPublicKey = encodePublicKey(publicKey);
            String stringPublicKeyHash = publicKeyHashFromStringPublicKey(stringPublicKey);
            String stringAddress = addressFromStringPublicKey(stringPublicKey);
            Account account = new Account(stringPrivateKey,stringPublicKey,stringPublicKeyHash,stringAddress);
            return account;
        } catch (Exception e) {
            logger.debug("生成账户失败。",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 私钥生成账户
     */
    public static Account accountFromStringPrivateKey(String stringPrivateKey) {
        try {
            if(!checkPrivateKey(stringPrivateKey)){
                throw new RuntimeException("私钥不合法。");
            }
            BigInteger bigIntegerPrivateKey = privateKeyFrom(stringPrivateKey);
            byte[] bytesPublicKey = publicKeyFromPrivateKey(bigIntegerPrivateKey);

            String stringPublicKey = encodePublicKey(bytesPublicKey);
            String stringPublicKeyHash = publicKeyHashFromStringPublicKey(stringPublicKey);
            String stringAddress = addressFromStringPublicKey(stringPublicKey);
            Account account = new Account(stringPrivateKey,stringPublicKey,stringPublicKeyHash,stringAddress);
            return account;
        } catch (Exception e) {
            logger.debug("从私钥恢复账户失败。",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥生成地址
     */
    public static String addressFromStringPublicKey(String stringPublicKey) {
        try {
            byte[] bytesPublicKey = HexUtil.hexStringToBytes(stringPublicKey);
            return base58AddressFromPublicKey(bytesPublicKey);
        } catch (Exception e) {
            logger.debug("公钥生成地址失败。",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥哈希生成地址
     */
    public static String addressFromStringPublicKeyHash(String stringPublicKeyHash) {
        try {
            byte[] bytesPublicKeyHash = HexUtil.hexStringToBytes(stringPublicKeyHash);
            return base58AddressFromBytesPublicKeyHash(bytesPublicKeyHash);
        } catch (Exception e) {
            logger.debug("公钥哈希生成地址失败。",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥生成公钥哈希
     */
    public static String publicKeyHashFromStringPublicKey(String stringPublicKey) {
        try {
            byte[] bytesPublicKey = publicKeyFrom(stringPublicKey);
            byte[] bytesPublicKeyHash = publicKeyHashFromPublicKey(bytesPublicKey);
            return HexUtil.bytesToHexString(bytesPublicKeyHash);
        } catch (Exception e) {
            logger.debug("公钥生成公钥哈希失败。",e);
            throw new RuntimeException(e);
        }
    }
    /**
     * 公钥生成公钥哈希
     * 对公钥进行两次哈希(第一次采用SHA256算法进行哈希，第二次采用RipeMD160算法进行哈希)得到的结果，就是公钥哈希
     */
    private static byte[] publicKeyHashFromPublicKey(byte[] publicKey) {
        byte[] bytesPublicKeyHash = Ripemd160Util.digest(Sha256Util.digest(publicKey));
        return bytesPublicKeyHash;
    }

    /**
     * 地址生成公钥哈希
     */
    public static String publicKeyHashFromStringAddress(String stringAddress) {
        try {
            byte[] bytesAddress = Base58Util.decode(stringAddress);
            byte[] bytesPublicKeyHash = new byte[20];
            ByteUtil.copy(bytesAddress, 1, bytesPublicKeyHash, 0, 20);
            return HexUtil.bytesToHexString(bytesPublicKeyHash);
        } catch (Exception e) {
            logger.debug("地址生成公钥哈希失败。",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 签名
     */
    public static String signature(String stringPrivateKey, String stringMessage) {
        try {
            BigInteger bigIntegerPrivateKey = privateKeyFrom(stringPrivateKey);
            byte[] bytesMessage = HexUtil.hexStringToBytes(stringMessage);
            byte[] bytesSignature = signature(bigIntegerPrivateKey,bytesMessage);
            return HexUtil.bytesToHexString(bytesSignature);
        } catch (Exception e) {
            logger.debug("签名出错。");
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证签名
     */
    public static boolean verifySignature(String stringPublicKey, String stringMessage, String stringSignature) {
        try {
            byte[] bytesPublicKey = publicKeyFrom(stringPublicKey);
            byte[] bytesMessage = HexUtil.hexStringToBytes(stringMessage);
            byte[] bytesSignature = HexUtil.hexStringToBytes(stringSignature);
            return verifySignature(bytesPublicKey,bytesMessage,bytesSignature);
        }catch(Exception e) {
            logger.debug("验证签名出错。");
            return false;
        }
    }


    /**
     * 校验是否是合法的私钥
     */
    public static boolean checkPrivateKey(String stringPrivateKey){
        return stringPrivateKey.length()==64 && HexUtil.checkHexString(stringPrivateKey);
    }

    /**
     * 由原始私钥推导出原始公钥
     */
    private static byte[] publicKeyFromPrivateKey(BigInteger bigIntegerPrivateKey) {
        byte[] bytePublicKey = CURVE.getG().multiply(bigIntegerPrivateKey).getEncoded(COMPRESSED);
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
    private static byte[] publicKeyFrom(String stringPublicKey) {
        byte[] bytesPublicKey = HexUtil.hexStringToBytes(stringPublicKey);
        return bytesPublicKey;
    }
    /**
     * 将原始私钥进行编码操作，生成编码私钥
     */
    private static String encodePrivateKey(BigInteger bigIntegerPrivateKey) {
        String hexPrivateKey = bigIntegerPrivateKey.toString(16);
        return formatPrivateKey(hexPrivateKey);
    }

    /**
     * 将原始公钥进行编码操作，生成编码公钥
     */
    private static String encodePublicKey(byte[] bytesPublicKey) {
        String stringPublicKey = HexUtil.bytesToHexString(bytesPublicKey);
        return stringPublicKey;
    }

    /**
     * 签名
     */
    private static byte[] signature(BigInteger bigIntegerPrivateKey, byte[] message) {
        try {
            ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bigIntegerPrivateKey, CURVE);
            signer.init(true, ecPrivateKeyParameters);
            BigInteger[] sigs = signer.generateSignature(message);
            BigInteger s = sigs[1];
            if (!isCanonical(s)) {
                s = CURVE.getN().subtract(s);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new ASN1Integer(sigs[0]));
            seq.addObject(new ASN1Integer(s));
            seq.close();
            return bos.toByteArray();
        } catch (Exception e) {
            logger.debug("签名出错。");
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证签名
     */
    private static boolean verifySignature(byte[] publicKey, byte[] message, byte[] signature) {
        try {
            ECDSASigner signer = new ECDSASigner();
            ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKey), CURVE);
            signer.init(false, ecPublicKeyParameters);
            ASN1InputStream decoder = new ASN1InputStream(signature);
            DLSequence seq = (DLSequence) decoder.readObject();
            ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
            ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
            if (!isCanonical(s.getValue())) {
                return false;
            }
            decoder.close();
            return signer.verifySignature(message, r.getValue(), s.getValue());
        } catch (Exception e) {
            logger.debug("验证签名，出错。");
            return false;
        }
    }

    /**
     * 公钥生成base58格式地址
     */
    private static String base58AddressFromPublicKey(byte[] bytesPublicKey) {
        byte[] publicKeyHash = publicKeyHashFromPublicKey(bytesPublicKey);
        return base58AddressFromBytesPublicKeyHash(publicKeyHash);
    }

    /**
     * 公钥哈希生成base58格式地址
     */
    private static String base58AddressFromBytesPublicKeyHash(byte[] bytesPublicKeyHash) {

        //地址版本号(1个字节)与公钥哈希(20个字节)
        byte[] bytesVersionAndPublicKeyHash = ByteUtil.concat(new byte[]{VERSION},bytesPublicKeyHash);

        //地址校验码(4个字节)
        byte[] bytesCheckCode = ByteUtil.get(Sha256Util.doubleDigest(bytesVersionAndPublicKeyHash), 0, 4);

        //地址(25个字节)=地址版本号(1个字节)+公钥哈希(20个字节)+地址校验码(4个字节)
        byte[] bytesAddress = ByteUtil.concat(bytesVersionAndPublicKeyHash,bytesCheckCode);

        //用Base58编码地址
        String base58Address = Base58Util.encode(bytesAddress);
        return base58Address;
    }

    /**
     * 格式化私钥
     * 前置填零，返回[长度为64位][十六进制字符串][形式的]私钥
     */
    private static String formatPrivateKey(String privateKey) {
        //私钥长度是256bit，64位十六进制的字符串数，如果私钥的长度不够，这里进行前置补零进行格式化。
        return StringUtil.prefixPadding(privateKey,64,"0");
    }

    /**
     * 是否是合法的地址
     */
    public static boolean isPayToPublicKeyHashAddress(String address) {
        try {
            byte[] bytesAddress = Base58.decode(address);
            byte[] bytePublicKeyHash = new byte[20];
            ByteUtil.copy(bytesAddress, 1, bytePublicKeyHash, 0, 20);
            String base58Address = addressFromStringPublicKeyHash(HexUtil.bytesToHexString(bytePublicKeyHash));
            return base58Address.equals(address);
        }catch (Exception e){
            logger.debug(String.format("地址[%s]不是base58格式的地址。",address));
            return false;
        }
    }
}