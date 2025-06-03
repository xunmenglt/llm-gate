package com.xunmeng.llmgate.proxy.error;

public class BadRequestException extends BaseLLMGateException {
    public BadRequestException(String message) {
        super("请求参数错误: " + message, 40001);
    }
}
