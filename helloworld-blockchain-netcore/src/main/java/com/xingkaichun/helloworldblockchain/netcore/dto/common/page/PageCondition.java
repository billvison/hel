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

    public static final PageCondition DEFAULT_PAGE_CONDITION = new PageCondition(1L,10L);




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
