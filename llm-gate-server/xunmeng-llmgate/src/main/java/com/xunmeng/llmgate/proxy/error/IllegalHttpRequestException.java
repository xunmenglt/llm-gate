package com.xunmeng.llmgate.proxy.error;

public class IllegalHttpRequestException extends BaseLLMGateException {
    public IllegalHttpRequestException(String detail) {
        super("非法 HTTP 请求: " + detail, 40002);
    }
}
