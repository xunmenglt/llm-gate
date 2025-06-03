package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.common.utils.spring.SpringUtils;
import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.error.ModelNotFoundException;
import com.xunmeng.llmgate.proxy.error.ProviderNotFoundException;
import com.xunmeng.llmgate.service.strategy.ModelProviderSelector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ModelProviderSelectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ModelProviderSelector modelProviderSelector = SpringUtils.getBean(ModelProviderSelector.class);

        InvokingContext invokingContext = ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();

        if (invokingContext == null || invokingContext.getModelName() == null) {
            throw new ModelNotFoundException("请求缺少模型名，无法选择服务商");
        }

        // 选择模型服务商
        ModelProvuider provider = modelProviderSelector.selectProviderByModel(invokingContext.getModelName());

        if (provider==null){
            throw new ProviderNotFoundException();
        }

        invokingContext.setModelProvuider(provider);

        ctx.fireChannelRead(msg);
    }
}
