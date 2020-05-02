package com.xingkaichun.helloworldblockchain.node.dto.wallet;

import lombok.Data;

/**
 * 钱包
 *
 * @author 邢开春 xingkaichun@qq.com
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
