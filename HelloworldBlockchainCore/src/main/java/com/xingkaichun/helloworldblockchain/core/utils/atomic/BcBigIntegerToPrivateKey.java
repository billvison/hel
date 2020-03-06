package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class BcBigIntegerToPrivateKey {


    public static PrivateKey getPrivateKeyFromECBigIntAndCurve(BigInteger s, String curveName) throws NoSuchProviderException {
        ECNamedCurveParameterSpec ecCurve = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curveName);
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, ecCurve);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC",org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

}
