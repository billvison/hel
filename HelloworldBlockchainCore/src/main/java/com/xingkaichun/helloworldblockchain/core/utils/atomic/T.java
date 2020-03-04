package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Collections;

public class T {


    public static PrivateKey getPrivateKeyFromECBigIntAndCurve(BigInteger s, String curveName) {
        X9ECParameters ecCurve = ECNamedCurveTable.getByName(curveName);
        ECParameterSpec ecParameterSpec = new ECNamedCurveSpec(curveName, ecCurve.getCurve(), ecCurve.getG(), ecCurve.getN(), ecCurve.getH(), ecCurve.getSeed());
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, ecParameterSpec);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ECPrivateKey PrivateFromPrivate(final ECPrivateKey privateKey) throws Exception {
        ECParameterSpec params = privateKey.getParams();
        ECPrivateKeySpec keySpec = new ECPrivateKeySpec(privateKey.getS(),tryFindNamedCurveSpec(params));
        return (ECPrivateKey) KeyFactory
                .getInstance("EC", org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME)
                .generatePrivate(keySpec);
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
