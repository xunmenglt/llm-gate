package com.xunmeng.llmgate.proxy.error;

/**
 * 授权失败异常（如未登录、无权限）
 */
public class LLMGateAuthorizationException extends BaseLLMGateException {

    public static final int DEFAULT_CODE = 40300;

    public LLMGateAuthorizationException() {
        super("权限不足，拒绝访问",DEFAULT_CODE);
    }

    public LLMGateAuthorizationException(String message) {
        super(message,DEFAULT_CODE);
    }
}
