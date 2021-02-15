package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

/**
 * JavaCryptographyExtension Provider工具类
 *
 * @author 邢开春
 */
public class JavaCryptographyExtensionProviderUtil {

    public static synchronized void addBouncyCastleProvider(){
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if(provider == null){
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    public static String getBouncyCastleProviderName(){
        return BouncyCastleProvider.PROVIDER_NAME;
    }
}
