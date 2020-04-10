package com.xingkaichun.helloworldblockchain.crypto.model;


import lombok.Data;

@Data
public class StringKey {

    private StringPrivateKey stringPrivateKey;
    private StringPublicKey stringPublicKey;
    private StringAddress stringAddress;
}
