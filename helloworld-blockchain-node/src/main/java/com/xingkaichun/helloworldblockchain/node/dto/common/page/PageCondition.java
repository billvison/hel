package com.xingkaichun.helloworldblockchain.node.dto.common.page;

import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
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
}
