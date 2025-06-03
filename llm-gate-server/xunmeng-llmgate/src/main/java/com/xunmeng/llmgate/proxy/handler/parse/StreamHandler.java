package com.xunmeng.llmgate.proxy.handler.parse;

import com.xunmeng.common.utils.spring.SpringUtils;
import com.xunmeng.llmgate.enums.LlmErrorType;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.service.ILlmUsageStatsLogService;
import com.xunmeng.llmgate.service.IMultiplierService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class StreamHandler extends ChannelInboundHandlerAdapter {

    protected long inputTokens=0L;

    protected long outputTokens=0L;

    protected double quota=0.0;

    // 是否有错误
    protected boolean hasError=false;

    protected LlmErrorType errorType;

    private ExecutorService logExecutorService = Executors.newSingleThreadExecutor();

    abstract String parseLineText(String line);

    abstract void caculateUsage(String line);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel().attr(ChannelAttributes.CLIENT_CHANNEL_KEY).get();
        InvokingContext invokingContext = clientChannel.attr(ChannelAttributes.INVOKING_CONTEXT).get();
        if (ObjectUtils.isNotEmpty(invokingContext)){
            String providerId=invokingContext.getModelProvuider().getProviderId();
            String modelName=invokingContext.getModelProvuider().getModelMapping().getModelName();
            String apiKey=invokingContext.getApiKey().getKey();
            ILlmUsageStatsLogService logService = SpringUtils.getBean(ILlmUsageStatsLogService.class);
            if (hasError){
                logExecutorService.submit(()->{
                    logService.doErrorLog(providerId,modelName,apiKey,errorType);
                });
            }else {
                if (inputTokens>0||outputTokens>0){
                    logExecutorService.submit(()->{
                        // 获取倍率
                        IMultiplierService multiplierService = SpringUtils.getBean(IMultiplierService.class);
                        quota=multiplierService.calculatePrice(modelName,inputTokens,outputTokens);
                        logService.doSuccessLog(providerId,modelName,apiKey,inputTokens,outputTokens,quota);
                    });
                }
            }
        }
    }
}
