package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryUtxosByAddressRequest {

    private String address;

    private PageCondition pageCondition;




    //region get set

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PageCondition getPageCondition() {
        return pageCondition;
    }

    public void setPageCondition(PageCondition pageCondition) {
        this.pageCondition = pageCondition;
    }


    //endregion
}
