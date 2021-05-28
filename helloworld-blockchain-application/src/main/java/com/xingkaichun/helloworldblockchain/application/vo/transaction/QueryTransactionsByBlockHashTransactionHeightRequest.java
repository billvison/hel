package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import com.xingkaichun.helloworldblockchain.application.vo.framwork.PageCondition;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionsByBlockHashTransactionHeightRequest {

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
