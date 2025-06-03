package com.xunmeng.llmgate.proxy.handler.factory;

import com.xunmeng.llmgate.enums.ModelProviderType;
import com.xunmeng.llmgate.proxy.handler.parse.OpenAIStreamHandler;
import com.xunmeng.llmgate.proxy.handler.parse.StreamHandler;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class StreamHandlerFactory {

    private static final Map<ModelProviderType, Class<? extends StreamHandler>> handlerMap=new HashMap<>();

    static {
        handlerMap.put(ModelProviderType.OPENAI, OpenAIStreamHandler.class);
    }

    public static StreamHandler build(ModelProviderType providerType) {
        Class<? extends StreamHandler> handlerClass = handlerMap.get(providerType);
        try {
            Constructor<? extends StreamHandler> constructor = handlerClass.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No no-arg constructor found for " + handlerClass.getSimpleName(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create StreamHandler instance", e);
        }
    }

}
