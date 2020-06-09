package com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.request;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SetMinerAddressRequest {

    private String minerAddress;




    //region get set
    public String getMinerAddress() {
        return minerAddress;
    }

    public void setMinerAddress(String minerAddress) {
        this.minerAddress = minerAddress;
    }
    //endregion
}
