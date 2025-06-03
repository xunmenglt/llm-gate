package com.xunmeng.llmgate.proxy.error;

public class ProviderNotFoundException extends BaseLLMGateException {
    public ProviderNotFoundException() {
        super("服务商不存在", 40402);
    }
}
