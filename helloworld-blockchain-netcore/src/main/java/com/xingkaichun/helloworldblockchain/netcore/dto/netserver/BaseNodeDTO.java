package com.xingkaichun.helloworldblockchain.netcore.dto.netserver;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BaseNodeDTO {

    private String ip;

    public BaseNodeDTO() {
    }

    public BaseNodeDTO(String ip) {
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
