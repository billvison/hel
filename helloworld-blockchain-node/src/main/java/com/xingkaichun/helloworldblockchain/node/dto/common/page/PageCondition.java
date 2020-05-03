package com.xingkaichun.helloworldblockchain.node.dto.common.page;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class PageCondition {

    private Long from;

    private Long size;

    public PageCondition() {
    }

    public PageCondition(Long from, Long size) {
        this.from = from;
        this.size = size;
    }

    public static PageCondition defaultPageCondition = new PageCondition(Long.valueOf(1),Long.valueOf(10));




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
