package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeDTO {

    private String ip;

    public NodeDTO() {
    }
    public NodeDTO(String ip) {
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
