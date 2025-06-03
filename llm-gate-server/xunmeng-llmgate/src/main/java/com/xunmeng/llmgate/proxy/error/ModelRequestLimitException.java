package com.xunmeng.llmgate.proxy.error;

public class ModelRequestLimitException extends  BaseLLMGateException{
    public static final int DEFAULT_CODE = 42902;

    public ModelRequestLimitException(String message) {
        super(message,DEFAULT_CODE);
    }

    public ModelRequestLimitException() {
        super("请求超过并发限制",DEFAULT_CODE);
    }

}
