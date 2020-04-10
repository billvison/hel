package com.xingkaichun.helloworldblockchain.crypto.model;


import lombok.Data;

import java.security.PublicKey;

@Data
public class HelloWorldPublicEcKey {

    private StringPublicKey stringPublicKey;
    private PublicKey publicKey;
}
