package com.xunmeng.llmgate.proxy.error;

/**
 * 服务或接口未实现异常（如模型未上线、API 未开放等）
 */
public class LLMGateNotImplementedException extends BaseLLMGateException {

    public static final int DEFAULT_CODE = 50100;

    public LLMGateNotImplementedException() {
        super("请求的服务尚未实现",DEFAULT_CODE);
    }

    public LLMGateNotImplementedException(String message) {
        super(message,DEFAULT_CODE);
    }
}
