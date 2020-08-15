package com.xingkaichun.helloworldblockchain.netcore.dto.common.page;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
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

    public final static PageCondition DEFAULT_PAGE_CONDITION = new PageCondition(Long.valueOf(1),Long.valueOf(10));




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
