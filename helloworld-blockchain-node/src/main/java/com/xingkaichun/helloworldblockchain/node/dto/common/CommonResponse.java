package com.xingkaichun.helloworldblockchain.node.dto.common;

import lombok.Data;

@Data
public class CommonResponse<T> {

    private T data;
}