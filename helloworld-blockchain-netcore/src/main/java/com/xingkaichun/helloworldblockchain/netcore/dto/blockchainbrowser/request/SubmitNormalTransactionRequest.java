package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.NormalTransactionDto;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SubmitNormalTransactionRequest {

    private NormalTransactionDto normalTransactionDto;




    //region get set

    public NormalTransactionDto getNormalTransactionDto() {
        return normalTransactionDto;
    }

    public void setNormalTransactionDto(NormalTransactionDto normalTransactionDto) {
        this.normalTransactionDto = normalTransactionDto;
    }

    //endregion
}
