package com.xunmeng.llmgate.proxy.error;

public class ProviderRequestLimitException extends  BaseLLMGateException{
    public static final int DEFAULT_CODE = 42901;

    public ProviderRequestLimitException(String message) {
        super(message,DEFAULT_CODE);
    }

    public ProviderRequestLimitException() {
        super("请求超过并发限制",DEFAULT_CODE);
    }

}
