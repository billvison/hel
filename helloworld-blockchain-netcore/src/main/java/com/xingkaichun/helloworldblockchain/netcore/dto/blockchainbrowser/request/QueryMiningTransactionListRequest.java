package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.common.page.PageCondition;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryMiningTransactionListRequest {

    private PageCondition pageCondition;




    //region get set

    public PageCondition getPageCondition() {
        return pageCondition;
    }

    public void setPageCondition(PageCondition pageCondition) {
        this.pageCondition = pageCondition;
    }

    //endregion
}
