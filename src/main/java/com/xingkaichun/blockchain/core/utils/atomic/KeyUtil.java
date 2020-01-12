package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.wallet.Wallet;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {

    public static byte[] decode(PublicKeyString publicKeyString) {
        return Base64.getDecoder().decode(publicKeyString.getValue());
    }

    public static byte[] decode(PrivateKeyString privateKeyString) {
        return Base64.getDecoder().decode(privateKeyString.getValue());
    }

    public static PublicKey convertPublicKeyStringToPublicKey(PublicKeyString publicKeyString) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] bytesPublicKey = decode(publicKeyString);

            final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey);
            final PublicKey pubKey = kf.generatePublic(pubKeySpec);
            return pubKey;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey convertPrivateKeyStringToPrivateKey(PrivateKeyString privateKeyString) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] bytesPrivateKey = decode(privateKeyString);

            final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            final PKCS8EncodedKeySpec encPrivKeySpec = new PKCS8EncodedKeySpec(bytesPrivateKey);
            final PrivateKey privKey = kf.generatePrivate(encPrivKeySpec);
            return privKey;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKeyString convertPublicKeyToPublicKeyString(PublicKey publicKey) {
        String encode = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        PublicKeyString publicKeyString = new PublicKeyString(encode);
        return publicKeyString;
    }

    public static PrivateKeyString convertPrivateKeyToPrivateKeyString(PrivateKey privateKey) {
        String encode = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        PrivateKeyString privateKeyString = new PrivateKeyString(encode);
        return privateKeyString;
    }


    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        Wallet wallet = WalletUtil.generateWallet();
        System.out.println("PrivateKeyString"+wallet.getPrivateKeyString().getValue());
        //System.out.println("PublicKeyString"+wallet.getPublicKeyString().getValue());

        byte[] bytesPrivateKey = decode(wallet.getPrivateKeyString());

        final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
        final PKCS8EncodedKeySpec encPrivKeySpec = new PKCS8EncodedKeySpec(bytesPrivateKey);
        final PrivateKey privKey = kf.generatePrivate(encPrivKeySpec);
        System.out.println("PrivateKeyString" + convertPrivateKeyToPrivateKeyString(privKey).getValue());

        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey)privKey;
       // ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(bcecPrivateKey.getD(),bcecPrivateKey.getParameters());
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(bcecPrivateKey.getS(),bcecPrivateKey.getParameters());
        //ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPrivateKeySpec.getParams().getG(),ecPrivateKeySpec.getParams());
        KeyFactory kf2 = KeyFactory.getInstance("ECDSA", "BC");
        PrivateKey privateKey2 = kf2.generatePrivate(ecPrivateKeySpec);
        //PublicKey publicKey2 = kf.generatePublic(ecPublicKeySpec);

        System.out.println("PrivateKeyString"+convertPrivateKeyToPrivateKeyString(privateKey2).getValue());
        //System.out.println("PublicKeyString"+convertPublicKeyToPublicKeyString(publicKey2).getValue());

    }
}
