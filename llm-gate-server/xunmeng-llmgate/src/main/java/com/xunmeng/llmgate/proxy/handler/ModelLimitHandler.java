package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.error.LLMGateRequestLimitException;
import com.xunmeng.llmgate.proxy.error.ModelRequestLimitException;
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
public class ModelLimitHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String,AtomicInteger> currentCountMap=new ConcurrentHashMap<>();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokingContext invokingContext = ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();
        ModelProvuider modelProvuider = invokingContext.getModelProvuider();
        String concurrentID = getConcurrentID(modelProvuider);
        AtomicInteger atomic = currentCountMap.computeIfAbsent(concurrentID, k -> new AtomicInteger(0));
        int current = atomic.incrementAndGet();
        if (modelProvuider.getModelMapping().getMaxConcurrency()>0 && current > modelProvuider.getModelMapping().getMaxConcurrency()) {
            throw new ModelRequestLimitException(String.format("服务商【%s】提供的模型【%s】连接数超过最大限制，拒绝连接！当前连接数：%s", modelProvuider.getProviderName(),modelProvuider.getModelMapping().getModelName(),current-1));
        }else {
            log.info("新连接建立，当前连接数：{}", current);
            super.channelRead(ctx,msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InvokingContext invokingContext = ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();
        boolean isToNextInactive=true; // 根据当前状态是否属于阻塞时，决定是否执行接下来的channel inactive
        if (invokingContext!=null){
            ModelProvuider modelProvuider = invokingContext.getModelProvuider();
            String concurrentID = getConcurrentID(modelProvuider);
            AtomicInteger atomicInteger = currentCountMap.get(concurrentID);
            if (atomicInteger != null) {
                if (modelProvuider.getModelMapping().getMaxConcurrency()>0 && atomicInteger.get() > modelProvuider.getModelMapping().getMaxConcurrency()) {
                    isToNextInactive=false;
                }
                int val = atomicInteger.decrementAndGet();
                log.info("连接关闭，当前连接数：{}", val);
                currentCountMap.put(concurrentID,atomicInteger);
            }else {
                isToNextInactive=false;
            }
        }
        if (isToNextInactive){
            super.channelInactive(ctx);
        }
    }

    private String getConcurrentID(ModelProvuider provuider){
        String providerId = provuider.getProviderId();
        ProvuiderModelMapping modelMapping = provuider.getModelMapping();
        String modelName = modelMapping.getModelName();
        String concurrentId=String.format("%s-%s",providerId,modelName);
        return concurrentId;
    }
}
