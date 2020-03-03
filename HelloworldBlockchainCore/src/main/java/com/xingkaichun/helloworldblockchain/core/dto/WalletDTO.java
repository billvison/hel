package com.xingkaichun.helloworldblockchain.core.dto;

import lombok.Data;

/**
 * 钱包
 */
@Data
public class WalletDTO {

    private String privateKey;

    private String publicKey;

    private String address;

    public WalletDTO(String privateKey, String publicKey, String address) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = address;
    }

}
