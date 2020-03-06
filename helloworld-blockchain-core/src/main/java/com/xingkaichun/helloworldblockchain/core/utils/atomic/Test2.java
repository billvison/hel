package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

public class Test2 {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("prime239v1");
        ECParameterSpec params = new ECNamedCurveSpec(ecNamedCurveParameterSpec.getName(),
                ecNamedCurveParameterSpec.getCurve(),
                ecNamedCurveParameterSpec.getG(),
                ecNamedCurveParameterSpec.getN(),
                ecNamedCurveParameterSpec.getH());

        ECPrivateKeySpec priKeySpec = new ECPrivateKeySpec(
                new BigInteger("876300101507107567501066130761671078357010671067781776716671676178726722"), // d
                //new BigInteger("149151612694928277647226440654718459829921417969033258893789033188931348"), // d
                params);

        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(
                ECPointUtil.decodePoint(
                        params.getCurve(),
                        Hex.decode("025b6dc53bc61a2548ffb0f671472de6c9521a9d2d2534e65abfcbd5fe0c70")), // Q
                params);

        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        PrivateKey privateKey = keyFactory.generatePrivate(priKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

        String encodePrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String encodePublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        System.out.println(encodePrivateKey);
        System.out.println(encodePublicKey);

        String msg = "11111111";
        byte[] sig = CipherUtil.applyECDSASig(privateKey,msg);
        System.out.println(CipherUtil.verifyECDSASig(publicKey,msg,sig));

    }
}
