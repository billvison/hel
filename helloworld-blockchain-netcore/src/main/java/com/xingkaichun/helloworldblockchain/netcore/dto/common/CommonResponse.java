package com.xingkaichun.helloworldblockchain.netcore.dto.common;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class CommonResponse<T> {

    private T data;




    //region get set

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //endregion
}