package com.xingkaichun.t;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@Builder
public class KeyContext {
    private String privateKeyStr;
    private PrivateKey privateKey;
    private String publicKeyStr;
    private PublicKey publicKey;
}