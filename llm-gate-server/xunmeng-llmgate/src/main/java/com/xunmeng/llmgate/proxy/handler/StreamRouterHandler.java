package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.llmgate.enums.ModelProviderType;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.handler.factory.StreamHandlerFactory;
import com.xunmeng.llmgate.proxy.handler.parse.StreamHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;



@Slf4j
public class StreamRouterHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel clientChannel = ctx.channel().attr(ChannelAttributes.CLIENT_CHANNEL_KEY).get();
        if (ObjectUtils.isNotEmpty(clientChannel)){
            InvokingContext invokingContext = clientChannel.attr(ChannelAttributes.INVOKING_CONTEXT).get();
            if (ObjectUtils.isNotEmpty(invokingContext)){
                String type = invokingContext.getModelProvuider().getType();
                ModelProviderType providerType = ModelProviderType.fromCodeSafe(type);
                StreamHandler handler = StreamHandlerFactory.build(providerType);
                ctx.pipeline().addAfter(ctx.name(), "streamHandler", handler);
                ctx.pipeline().remove(this);
            }
        }
        super.channelRead(ctx,msg);
    }
}