package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

import com.xingkaichun.helloworldblockchain.netcore.dto.common.PageCondition;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryTransactionListByBlockHashTransactionHeightRequest {

    private String blockHash;
    private PageCondition pageCondition;




    //region get set


    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public PageCondition getPageCondition() {
        return pageCondition;
    }

    public void setPageCondition(PageCondition pageCondition) {
        this.pageCondition = pageCondition;
    }

    //endregion
}
