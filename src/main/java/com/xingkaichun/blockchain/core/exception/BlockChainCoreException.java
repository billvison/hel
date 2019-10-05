package com.xingkaichun.blockchain.core.exception;

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