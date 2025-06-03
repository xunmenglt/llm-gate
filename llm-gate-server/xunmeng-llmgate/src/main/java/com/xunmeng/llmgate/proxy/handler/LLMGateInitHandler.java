package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.LLMGateContext;
import com.xunmeng.llmgate.proxy.error.LLMGateRequestLimitException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ChannelHandler.Sharable
@Component
public class LLMGateInitHandler extends ChannelInboundHandlerAdapter {

    @Value("${llmgate.server.port}")
    private int port;

    @Value("${llmgate.server.prefix}")
    private String prefix;

    @Value("${llmgate.mianThreadNum}")
    private int mianThreadNum;

    @Value("${llmgate.workThreadNum}")
    private int workThreadNum;

    @Value("${llmgate.timeOut}")
    private int timeOut;

    @Value("${llmgate.maxConnections}")
    private int maxConnections;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LLMGateContext llmGateContext = new LLMGateContext(port, prefix, mianThreadNum, workThreadNum, timeOut, maxConnections);
        ctx.channel().attr(ChannelAttributes.LLMGATE_CONTEXT).set(llmGateContext);
        super.channelActive(ctx);
    }
}
