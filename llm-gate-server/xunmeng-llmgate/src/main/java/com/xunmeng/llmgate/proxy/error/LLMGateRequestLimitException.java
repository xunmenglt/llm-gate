package com.xunmeng.llmgate.proxy.error;

public class LLMGateRequestLimitException extends  BaseLLMGateException{
    public static final int DEFAULT_CODE = 42900;

    public LLMGateRequestLimitException(String message) {
        super(message,DEFAULT_CODE);
    }

    public LLMGateRequestLimitException() {
        super("请求超过并发限制",DEFAULT_CODE);
    }

}
