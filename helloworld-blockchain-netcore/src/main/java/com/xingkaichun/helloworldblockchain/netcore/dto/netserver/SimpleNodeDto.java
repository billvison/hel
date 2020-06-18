package com.xingkaichun.helloworldblockchain.netcore.dto.netserver;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SimpleNodeDto {

    private String ip;
    private int port;

    public SimpleNodeDto() {
    }

    public SimpleNodeDto(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }




    //region get set

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    //endregion
}
