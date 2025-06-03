package com.xunmeng.llmgate.proxy.error;

public class BaseLLMGateException extends RuntimeException {
    private final int code;

    public BaseLLMGateException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
