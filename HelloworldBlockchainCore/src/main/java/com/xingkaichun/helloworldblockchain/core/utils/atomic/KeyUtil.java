package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.xingkaichun.helloworldblockchain.core.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPublicKey;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {

    public static byte[] decode(StringPublicKey stringPublicKey) {
        return Base64.getDecoder().decode(stringPublicKey.getValue());
    }

    public static byte[] decode(StringPrivateKey stringPrivateKey) {
        return Base64.getDecoder().decode(stringPrivateKey.getValue());
    }

    public static PublicKey convertStringPublicKeyToPublicKey(StringPublicKey stringPublicKey) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] bytesPublicKey = decode(stringPublicKey);

            final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey);
            final PublicKey pubKey = kf.generatePublic(pubKeySpec);
            return pubKey;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey convertStringPrivateKeyToPrivateKey(StringPrivateKey stringPrivateKey) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] bytesPrivateKey = decode(stringPrivateKey);

            final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            final PKCS8EncodedKeySpec encPrivKeySpec = new PKCS8EncodedKeySpec(bytesPrivateKey);
            final PrivateKey privKey = kf.generatePrivate(encPrivKeySpec);
            return privKey;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static StringPublicKey convertPublicKeyToStringPublicKey(PublicKey publicKey) {
        String encode = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        StringPublicKey stringPublicKey = new StringPublicKey(encode);
        return stringPublicKey;
    }

    public static StringPrivateKey convertPrivateKeyToStringPrivateKey(PrivateKey privateKey) {
        String encode = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        StringPrivateKey stringPrivateKey = new StringPrivateKey(encode);
        return stringPrivateKey;
    }

    public static boolean isStringPublicKeyEqualStringAddress(StringPublicKey stringPublicKey, StringAddress stringAddress) {
        try {
            StringAddress tempStringAddress = convertStringPublicKeyToStringAddress(stringPublicKey);
            return stringAddress.getValue().equals(tempStringAddress.getValue());
        } catch (Exception e) {
            return false;
        }
    }

    public static StringAddress convertStringPublicKeyToStringAddress(StringPublicKey stringPublicKey) throws Exception {
        String version = "00";
        String publicKeyHash =  CipherUtil.ripeMD160(CipherUtil.applySha256(stringPublicKey.getValue()));
        String check = CipherUtil.applySha256(CipherUtil.applySha256(version+publicKeyHash)).substring(0,4);
        String address = Base58Util.encode((version+publicKeyHash+check).getBytes());
        return new StringAddress(address);
    }
}
