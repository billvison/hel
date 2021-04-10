package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class API {

    /**
     * 接口常量
     */
    public static final String PING = "/ping";
    public static final String GET_BLOCK = "/get_block";
    public static final String POST_TRANSACTION = "/post_transaction";
    public static final String POST_BLOCK = "/post_block";

    /**
     * 请求返回结果常量
     */
    public static class Response{
        public static final String OK = "ok";
        public static final String ERROR = "";
    }
}
