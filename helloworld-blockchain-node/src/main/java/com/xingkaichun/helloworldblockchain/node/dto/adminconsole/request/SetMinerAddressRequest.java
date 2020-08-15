package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
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
