package com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.response;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
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
