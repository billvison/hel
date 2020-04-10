package com.xingkaichun.helloworldblockchain.crypto;


import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import lombok.Data;

import java.security.PrivateKey;

@Data
public class HelloWorldEcPrivateKey {

    private StringPrivateKey stringPrivateKey;
    private PrivateKey privateKey;
}
