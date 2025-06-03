package com.xunmeng.llmgate.proxy.attributes;

import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.context.LLMGateContext;
import com.xunmeng.llmgate.proxy.context.RequestContext;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class ChannelAttributes {
    public static final AttributeKey<RequestContext> REQUEST_CONTEXT =
            AttributeKey.valueOf("REQUEST_CONTEXT");

    public static final AttributeKey<LLMGateContext> LLMGATE_CONTEXT =
            AttributeKey.valueOf("LLMGATE_CONTEXT");

    public static final AttributeKey<InvokingContext> INVOKING_CONTEXT =
            AttributeKey.valueOf("INVOKING_CONTEXT");

    public static final AttributeKey<Channel> PROXY_CHANNEL_KEY=
            AttributeKey.valueOf("PROXY_CHANNEL_KEY");

    public static final AttributeKey<Channel> CLIENT_CHANNEL_KEY=
            AttributeKey.valueOf("CLIENT_CHANNEL_KEY");
}