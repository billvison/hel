package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Collections;

public class EcKeyUtil {

    public static byte[] decode(String key) {
        return Base64.getDecoder().decode(key);
    }


    public static PublicKey convertStringPublicKeyToPublicKey(StringPublicKey stringPublicKey) {
        return EcKeyUtil.convertStringPublicKeyToPublicKey(stringPublicKey.getValue());
    }

    public static PrivateKey convertStringPrivateKeyToPrivateKey(StringPrivateKey stringPrivateKey) {
        return EcKeyUtil.convertStringPrivateKeyToPrivateKey(stringPrivateKey.getValue());
    }

    public static PublicKey convertStringPublicKeyToPublicKey(String stringPublicKey) {
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

    public static PrivateKey convertStringPrivateKeyToPrivateKey(String stringPrivateKey) {
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

    public static String encodePublicKey(PublicKey publicKey) {
        String encode = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return encode;
    }

    public static String encodePrivateKey(PrivateKey privateKey) {
        String encode = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return encode;
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

    public static HelloWorldEcKey fromEncodePrivateKey(String encodePrivateKey) throws Exception {
        PrivateKey ecPrivateKey = EcKeyUtil.convertStringPrivateKeyToPrivateKey(new StringPrivateKey(encodePrivateKey));
        PublicKey ecPublicKey = publicFromPrivate((ECPrivateKey) ecPrivateKey);
        HelloWorldEcKey helloWorldEcKey = new HelloWorldEcKey();
        HelloWorldEcPrivateKey helloWorldEcPrivateKey = new HelloWorldEcPrivateKey();
        helloWorldEcPrivateKey.setPrivateKey(ecPrivateKey);
        helloWorldEcPrivateKey.setStringPrivateKey(new StringPrivateKey(encodePrivateKey));
        HelloWorldPublicEcKey helloWorldPublicEcKey = new HelloWorldPublicEcKey();
        helloWorldPublicEcKey.setPublicKey(ecPublicKey);
        helloWorldPublicEcKey.setStringPublicKey(new StringPublicKey(EcKeyUtil.encodePublicKey(ecPublicKey)));
        helloWorldEcKey.setHelloWorldEcPrivateKey(helloWorldEcPrivateKey);
        helloWorldEcKey.setHelloWorldPublicEcKey(helloWorldPublicEcKey);
        return helloWorldEcKey;
    }

    public static HelloWorldEcKey randomHelloWorldEcKey() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGen.initialize(ecSpec, random);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        HelloWorldEcKey helloWorldEcKey = new HelloWorldEcKey();
        HelloWorldEcPrivateKey helloWorldEcPrivateKey = new HelloWorldEcPrivateKey();
        helloWorldEcPrivateKey.setPrivateKey(privateKey);
        helloWorldEcPrivateKey.setStringPrivateKey(new StringPrivateKey(encodePrivateKey(privateKey)));
        HelloWorldPublicEcKey helloWorldPublicEcKey = new HelloWorldPublicEcKey();
        helloWorldPublicEcKey.setPublicKey(publicKey);
        helloWorldPublicEcKey.setStringPublicKey(new StringPublicKey(encodePublicKey(publicKey)));
        helloWorldEcKey.setHelloWorldEcPrivateKey(helloWorldEcPrivateKey);
        helloWorldEcKey.setHelloWorldPublicEcKey(helloWorldPublicEcKey);
        return helloWorldEcKey;
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
        String publicKeyHash =  CipherUtil.ripeMD160(CipherUtil.applySha256(stringPublicKey.getValue().getBytes()));
        String check = CipherUtil.applySha256(CipherUtil.applySha256((version+publicKeyHash).getBytes()).getBytes()).substring(0,4);
        String address = Base58Util.encode((version+publicKeyHash+check).getBytes());
        return new StringAddress(address);
    }
}
