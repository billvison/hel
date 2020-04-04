package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.WalletDTO;
import lombok.Data;

@Data
public class GenerateWalletResponse {

    private WalletDTO walletDTO;
}
