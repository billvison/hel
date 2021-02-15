package com.xingkaichun.helloworldblockchain.explorer.dto.synchronizer;

/**
 *
 * @author 邢开春 409060350@qq.com
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
