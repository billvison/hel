package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.model.key.StringPublicKey;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Collections;

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

    public static ECPublicKey publicFromPrivate(ECPrivateKey privateKey) throws Exception {
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
    public static ECParameterSpec tryFindNamedCurveSpec(ECParameterSpec params) {
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
