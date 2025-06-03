package com.xunmeng.llmgate.proxy.error;

/**
 * 未知服务器异常，用于统一兜底错误处理。
 */
public class ModelProviderServerException extends BaseLLMGateException {

    public static final int DEFAULT_CODE = 50001;

    public ModelProviderServerException(String message) {
        super(message,DEFAULT_CODE);
    }

    public ModelProviderServerException() {
        super("模型提供商服务异常",DEFAULT_CODE);
    }
}
