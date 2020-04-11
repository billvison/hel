package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Collections;

public class KeyUtil {

    public static PrivateKey privateKeyFrom(StringPrivateKey stringPrivateKey) {
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

    public static PublicKey publicKeyFrom(StringPublicKey stringPublicKey) {
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

    public static StringPublicKey stringPublicKeyFrom(PublicKey publicKey) {
        String encode = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return new StringPublicKey(encode);
    }

    public static StringPrivateKey stringPrivateKeyFrom(PrivateKey privateKey) {
        String encode = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return new StringPrivateKey(encode);
    }

    public static StringKey stringKeyFrom(StringPrivateKey stringPrivateKey) throws Exception {
        PrivateKey ecPrivateKey = KeyUtil.privateKeyFrom(stringPrivateKey);
        PublicKey ecPublicKey = publicFromPrivate((ECPrivateKey) ecPrivateKey);
        StringKey stringKey = new StringKey();

        StringPublicKey stringPublicKey = stringPublicKeyFrom(ecPublicKey);
        StringAddress stringAddress = stringAddressFrom(stringPublicKey);
        stringKey.setStringPrivateKey(stringPrivateKey);
        stringKey.setStringPublicKey(stringPublicKey);
        stringKey.setStringAddress(stringAddress);
        return stringKey;
    }

    public static StringKey randomStringKey() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGen.initialize(ecSpec, random);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        StringKey stringKey = new StringKey();
        StringPrivateKey stringPrivateKey = stringPrivateKeyFrom(privateKey);
        StringPublicKey stringPublicKey = stringPublicKeyFrom(publicKey);
        StringAddress stringAddress = stringAddressFrom(stringPublicKey);
        stringKey.setStringPrivateKey(stringPrivateKey);
        stringKey.setStringPublicKey(stringPublicKey);
        stringKey.setStringAddress(stringAddress);
        return stringKey;
    }

    public static StringAddress stringAddressFrom(StringPublicKey stringPublicKey) throws Exception {
        String version = "00";
        String publicKeyHash =  RipeMD160Util.ripeMD160(SHA256Util.applySha256(stringPublicKey.getValue()));
        String check = SHA256Util.applySha256(SHA256Util.applySha256((version+publicKeyHash))).substring(0,4);
        String address = Base58Util.encode((version+publicKeyHash+check).getBytes());
        return new StringAddress(address);
    }

    public static boolean isStringPublicKeyEqualStringAddress(StringPublicKey stringPublicKey, StringAddress stringAddress) {
        try {
            StringAddress tempStringAddress = stringAddressFrom(stringPublicKey);
            return stringAddress.getValue().equals(tempStringAddress.getValue());
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * ECDSA签名
     */
    public static String applyECDSASig(StringPrivateKey stringPrivateKey, String data) {
        try {
            PrivateKey privateKey = privateKeyFrom(stringPrivateKey);
            byte[] bytesSignature = applyECDSASig(privateKey,data.getBytes());
            String strSignature = Base64.getEncoder().encodeToString(bytesSignature);
            return strSignature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECDSA签名验证
     */
    public static boolean verifyECDSASig(StringPublicKey senderStringPublicKey, String data, String strSignature) {
        try {
            byte[] bytesSignature = Base64.getDecoder().decode(strSignature);
            PublicKey publicKey = publicKeyFrom(senderStringPublicKey);
            return verifyECDSASig(publicKey,data.getBytes(),bytesSignature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }









    /**
     * ECDSA签名
     */
    private static byte[] applyECDSASig(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("ECDSA", "BC");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] sign = signature.sign();
        return sign;
    }

    /**
     * ECDSA签名验证
     */
    private static boolean verifyECDSASig(PublicKey publicKey, byte[] data, byte[] signature) throws Exception {
        Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);
        return ecdsaVerify.verify(signature);
    }

    private static byte[] decode(StringPrivateKey stringPrivateKey) {
        return Base64.getDecoder().decode(stringPrivateKey.getValue());
    }

    private static byte[] decode(StringPublicKey stringPublicKey) {
        return Base64.getDecoder().decode(stringPublicKey.getValue());
    }

    private static ECPublicKey publicFromPrivate(ECPrivateKey privateKey) throws Exception {
        ECParameterSpec params = privateKey.getParams();
        org.bouncycastle.jce.spec.ECParameterSpec bcSpec = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util
                .convertSpec(params, false);
        org.bouncycastle.math.ec.ECPoint q = bcSpec.getG().multiply(privateKey.getS());
        org.bouncycastle.math.ec.ECPoint bcW = bcSpec.getCurve().decodePoint(q.getEncoded(false));
        ECPoint w = new ECPoint(
                bcW.getAffineXCoord().toBigInteger(),
                bcW.getAffineYCoord().toBigInteger());
        ECPublicKeySpec keySpec = new ECPublicKeySpec(w, tryFindNamedCurveSpec(params));
        return (ECPublicKey) KeyFactory
                .getInstance("EC", org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME)
                .generatePublic(keySpec);
    }

    @SuppressWarnings("unchecked")
    private static ECParameterSpec tryFindNamedCurveSpec(ECParameterSpec params) {
        org.bouncycastle.jce.spec.ECParameterSpec bcSpec
                = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertSpec(params, false);
        for (Object name : Collections.list(org.bouncycastle.jce.ECNamedCurveTable.getNames())) {
            org.bouncycastle.jce.spec.ECNamedCurveParameterSpec bcNamedSpec
                    = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec((String) name);
            if (bcNamedSpec.getN().equals(bcSpec.getN())
                    && bcNamedSpec.getH().equals(bcSpec.getH())
                    && bcNamedSpec.getCurve().equals(bcSpec.getCurve())
                    && bcNamedSpec.getG().equals(bcSpec.getG())) {
                return new org.bouncycastle.jce.spec.ECNamedCurveSpec(
                        bcNamedSpec.getName(),
                        bcNamedSpec.getCurve(),
                        bcNamedSpec.getG(),
                        bcNamedSpec.getN(),
                        bcNamedSpec.getH(),
                        bcNamedSpec.getSeed());
            }
        }
        return params;
    }
}
