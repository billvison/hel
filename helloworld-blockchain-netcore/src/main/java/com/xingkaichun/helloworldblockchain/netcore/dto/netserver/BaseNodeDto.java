package com.xingkaichun.helloworldblockchain.netcore.dto.netserver;

/**
 *
 * @author 邢开春
 */
public class BaseNodeDto {

    private String ip;

    public BaseNodeDto() {
    }

    public BaseNodeDto(String ip) {
        this.ip = ip;
    }




    //region get set

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    //endregion
}
