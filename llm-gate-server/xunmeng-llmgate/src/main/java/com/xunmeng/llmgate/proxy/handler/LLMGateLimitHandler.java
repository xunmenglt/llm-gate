package com.xunmeng.llmgate.proxy.handler;

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
public class LLMGateLimitHandler extends ChannelInboundHandlerAdapter {

    @Value("${llmgate.maxConnections}")
    private int maxConnections;
    private final AtomicInteger currentConnections = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active: {}", ctx.channel().remoteAddress());
        int current = currentConnections.incrementAndGet();
        if (current > maxConnections) {
            currentConnections.decrementAndGet();
            throw new LLMGateRequestLimitException(String.format("连接数超过最大限制，拒绝连接！当前连接数：{}", current));
        }
        log.info("新连接建立，当前连接数：{}", current);
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        int current = currentConnections.decrementAndGet();
        log.info("连接关闭，当前连接数：{}", current);
        super.channelInactive(ctx);
    }
}
