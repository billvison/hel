package com.xingkaichun.helloworldblockchain.node.dto.miner;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class IsMinerActiveResponse {

    private boolean minerInActiveState;




    //region get set

    public boolean isMinerInActiveState() {
        return minerInActiveState;
    }

    public void setMinerInActiveState(boolean minerInActiveState) {
        this.minerInActiveState = minerInActiveState;
    }


    //endregion
}
