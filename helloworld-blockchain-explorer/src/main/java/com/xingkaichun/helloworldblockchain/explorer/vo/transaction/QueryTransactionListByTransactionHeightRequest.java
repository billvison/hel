package com.xingkaichun.helloworldblockchain.explorer.vo.transaction;

import com.xingkaichun.helloworldblockchain.netcore.dto.common.PageCondition;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionListByTransactionHeightRequest {

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
