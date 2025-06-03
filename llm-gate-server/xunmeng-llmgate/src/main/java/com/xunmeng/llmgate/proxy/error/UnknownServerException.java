package com.xunmeng.llmgate.proxy.error;

/**
 * 未知服务器异常，用于统一兜底错误处理。
 */
public class UnknownServerException extends BaseLLMGateException {

    public UnknownServerException(Throwable cause) {
        super("服务器内部错误: " + cause.getClass().getSimpleName(), 50000);
        initCause(cause);
    }
}
