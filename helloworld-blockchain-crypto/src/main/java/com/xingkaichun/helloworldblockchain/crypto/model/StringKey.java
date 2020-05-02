package com.xingkaichun.helloworldblockchain.crypto.model;


import lombok.Data;

/**
 * 秘钥
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class StringKey {

    private StringPrivateKey stringPrivateKey;
    private StringPublicKey stringPublicKey;
    private StringAddress stringAddress;
}
