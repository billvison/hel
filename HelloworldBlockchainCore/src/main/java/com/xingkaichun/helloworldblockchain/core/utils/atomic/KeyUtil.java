package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.xingkaichun.helloworldblockchain.core.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPublicKey;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
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
        System.out.println(address);
        return new StringAddress(address);
    }




    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        System.out.println(isStringPublicKeyEqualStringAddress(new StringPublicKey("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEP/4hnd01dsmpFKDbS6SKcSMHWm67vhNaiQK2niF/ASIUf8h3v/9+KhBgA8rZkD3H6Q8cjNmOnt3F71zqlZ7LHQ=="),
                new StringAddress("3Psn3vDDeEmjwN53SMYYQpDrhLFQc1esYezehoT5k3zvNHgLUmV8N8G")));
        System.out.println(isStringPublicKeyEqualStringAddress(new StringPublicKey("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEP/4hnd01dsmpFKDbS6SKcSMHWm67vhNaiQK2niF/ASIUf8h3v/9+KhBgA8rZkD3H6Q8cjNmOnt3F71zqlZ7LHQ=="),
                new StringAddress("3Psn3vDDeEmjwN53SMYYQpDrhLFQc1esYezehoT5k3zvNHgLUmV8N8G")));
/*        Wallet wallet = WalletUtil.generateWallet();
        System.out.println("StringPrivateKey"+wallet.getStringPrivateKey().getValue());
        //System.out.println("StringPublicKey"+wallet.getStringPublicKey().getValue());

        byte[] bytesPrivateKey = decode(wallet.getStringPrivateKey());

        final KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
        final PKCS8EncodedKeySpec encPrivKeySpec = new PKCS8EncodedKeySpec(bytesPrivateKey);
        final PrivateKey privKey = kf.generatePrivate(encPrivKeySpec);
        System.out.println("StringPrivateKey" + convertPrivateKeyToStringPrivateKey(privKey).getValue());

        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey)privKey;
       // ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(bcecPrivateKey.getD(),bcecPrivateKey.getParameters());
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(bcecPrivateKey.getS(),bcecPrivateKey.getParameters());
        //ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPrivateKeySpec.getParams().getG(),ecPrivateKeySpec.getParams());
        KeyFactory kf2 = KeyFactory.getInstance("ECDSA", "BC");
        PrivateKey privateKey2 = kf2.generatePrivate(ecPrivateKeySpec);
        //PublicKey publicKey2 = kf.generatePublic(ecPublicKeySpec);

        System.out.println("StringPrivateKey"+convertPrivateKeyToStringPrivateKey(privateKey2).getValue());
        //System.out.println("StringPublicKey"+convertPublicKeyToStringPublicKey(publicKey2).getValue());*/

    }
}
