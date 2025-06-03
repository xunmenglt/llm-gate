package com.xunmeng.llmgate.proxy.error;

public class ModelNotFoundException extends BaseLLMGateException {
    public ModelNotFoundException(String modelName) {
        super("模型不存在: " + modelName, 40401);
    }


}
