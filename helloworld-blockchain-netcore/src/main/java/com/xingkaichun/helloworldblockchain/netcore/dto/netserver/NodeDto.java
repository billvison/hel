package com.xingkaichun.helloworldblockchain.netcore.dto.netserver;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeDto extends BaseNodeDto {

    private Long blockchainHeight;
    private Boolean isNodeAvailable;
    private Integer errorConnectionTimes;
    private Boolean fork;




    //region get set


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
