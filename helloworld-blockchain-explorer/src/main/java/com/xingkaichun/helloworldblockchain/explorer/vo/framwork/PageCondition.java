package com.xingkaichun.helloworldblockchain.explorer.vo.framwork;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class PageCondition {

    private Long from;

    private Long size;

    public PageCondition() {
    }



    //region get set

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
    //endregion
}
