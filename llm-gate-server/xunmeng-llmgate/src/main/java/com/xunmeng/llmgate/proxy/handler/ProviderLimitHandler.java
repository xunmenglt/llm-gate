package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.error.LLMGateRequestLimitException;
import com.xunmeng.llmgate.proxy.error.ProviderRequestLimitException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ChannelHandler.Sharable
@Component
public class ProviderLimitHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String,AtomicInteger> currentCountMap=new ConcurrentHashMap<>();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokingContext invokingContext = ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();
        ModelProvuider modelProvuider = invokingContext.getModelProvuider();
        AtomicInteger atomic = currentCountMap.getOrDefault(modelProvuider.getProviderId(), new AtomicInteger(0));
        currentCountMap.put(modelProvuider.getProviderId(),atomic);
        int current = atomic.incrementAndGet();
        if (modelProvuider.getMaxConcurrency()>0 && current > modelProvuider.getMaxConcurrency()) {
            atomic.decrementAndGet();
            throw new ProviderRequestLimitException(String.format("服务商【{}】连接数超过最大限制，拒绝连接！当前连接数：{}", modelProvuider.getProviderName(),current));
        }
        log.info("新连接建立，当前连接数：{}", current);
        super.channelRead(ctx, msg);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InvokingContext invokingContext = ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();
        if (invokingContext!=null){
            ModelProvuider modelProvuider = invokingContext.getModelProvuider();
            String providerId = modelProvuider.getProviderId();
            AtomicInteger atomicInteger = currentCountMap.get(providerId);
            atomicInteger.decrementAndGet();
            currentCountMap.put(providerId,atomicInteger);
            log.info("连接关闭，当前连接数：{}", atomicInteger.get());
        }
        super.channelInactive(ctx);
    }
}
