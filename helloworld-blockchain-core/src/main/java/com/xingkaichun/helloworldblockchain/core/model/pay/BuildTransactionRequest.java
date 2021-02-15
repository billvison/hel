package com.xingkaichun.helloworldblockchain.core.model.pay;

import java.util.List;

/**
 *
 * @author 邢开春
 */
public class BuildTransactionRequest {

    private List<Recipient> recipientList ;



    //region get set

    public List<Recipient> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<Recipient> recipientList) {
        this.recipientList = recipientList;
    }


    //endregion
}
