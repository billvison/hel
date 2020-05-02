package com.xingkaichun.helloworldblockchain.node.dto.common;

import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class CommonResponse<T> {

    private T data;
}