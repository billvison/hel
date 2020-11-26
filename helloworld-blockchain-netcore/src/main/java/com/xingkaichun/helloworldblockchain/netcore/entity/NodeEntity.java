package com.xingkaichun.helloworldblockchain.netcore.entity;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class NodeEntity{
    private String ip;
    private Long blockchainHeight;
    private Boolean isNodeAvailable;
    private Integer errorConnectionTimes;
    private Boolean fork;




    //region get set
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

    public Boolean getIsNodeAvailable() {
        return isNodeAvailable;
    }

    public void setIsNodeAvailable(Boolean nodeAvailable) {
        isNodeAvailable = nodeAvailable;
    }

    public Integer getErrorConnectionTimes() {
        return errorConnectionTimes;
    }

    public void setErrorConnectionTimes(Integer errorConnectionTimes) {
        this.errorConnectionTimes = errorConnectionTimes;
    }

    public Boolean getFork() {
        return fork;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }

    //endregion
}
