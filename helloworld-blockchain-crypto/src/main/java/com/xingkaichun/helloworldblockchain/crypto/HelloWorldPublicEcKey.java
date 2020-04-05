package com.xingkaichun.helloworldblockchain.crypto;


import lombok.Data;

import java.security.PublicKey;

@Data
public class HelloWorldPublicEcKey {

    private String encodePublicKey;
    private PublicKey publicKey;
}
