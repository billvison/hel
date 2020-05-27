package com.xingkaichun.helloworldblockchain.core.exception;

public class ExecuteScriptException extends Exception{

    public ExecuteScriptException() {
        super();
    }

    public ExecuteScriptException(String message) {
        super(message);
    }

    public ExecuteScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}