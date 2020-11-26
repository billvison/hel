package com.xingkaichun.helloworldblockchain.node.dto.synchronizer;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class IsSynchronizerActiveResponse {

    private boolean synchronizerInActiveState;




    //region get set

    public boolean isSynchronizerInActiveState() {
        return synchronizerInActiveState;
    }

    public void setSynchronizerInActiveState(boolean synchronizerInActiveState) {
        this.synchronizerInActiveState = synchronizerInActiveState;
    }


    //endregion
}
