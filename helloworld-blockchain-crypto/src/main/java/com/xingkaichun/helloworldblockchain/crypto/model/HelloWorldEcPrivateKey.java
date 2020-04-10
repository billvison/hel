package com.xingkaichun.helloworldblockchain.crypto.model;


import lombok.Data;

import java.security.PrivateKey;

@Data
public class HelloWorldEcPrivateKey {

    private StringPrivateKey stringPrivateKey;
    private PrivateKey privateKey;
}
