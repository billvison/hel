package com.xingkaichun.helloworldblockchain.node.dto.blockchain.response;

import com.xingkaichun.helloworldblockchain.core.dto.WalletDTO;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import lombok.Data;

@Data
public class GenerateWalletResponse {

    private WalletDTO walletDTO;
}
