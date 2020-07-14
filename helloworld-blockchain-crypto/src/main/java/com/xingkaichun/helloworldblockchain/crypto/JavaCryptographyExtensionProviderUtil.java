package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

public class JavaCryptographyExtensionProviderUtil {

    public static synchronized void addBouncyCastleProvider(){
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if(provider == null){
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }
}
