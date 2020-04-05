package com.xingkaichun.helloworldblockchain.crypto;


import lombok.Data;

import java.security.PrivateKey;

@Data
public class HelloWorldEcPrivateKey {

    private String encodePrivateKey;
    private PrivateKey privateKey;
}
