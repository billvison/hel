package com.xingkaichun.helloworldblockchain.core.exception;

/**
 * 异常
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainCoreException extends RuntimeException {

    public BlockChainCoreException() {
        super();
    }

    public BlockChainCoreException(String message) {
        super(message);
    }

    public BlockChainCoreException(String message, Throwable cause) {
        super(message, cause);
    }
}