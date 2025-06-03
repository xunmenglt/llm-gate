package com.xunmeng.llmgate.proxy;

import com.xunmeng.llmgate.proxy.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * 代理启动入口
 */
@Configuration
public class LLMGateServer implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LLMGateServer.class);

    @Value("${llmgate.server.port}")
    private int port;

    @Value("${llmgate.mianThreadNum}")
    private int mianThreadNum;

    @Value("${llmgate.workThreadNum}")
    private int workThreadNum;

    @Value("${llmgate.timeOut}")
    private int timeOut;

    @Resource
    private LLMGateLimitHandler llmGateLimitHandler;
    @Resource
    private ProviderLimitHandler providerLimitHandler;
    @Resource
    private ModelLimitHandler modelLimitHandler;

    @Resource
    private LLMGateInitHandler llmGateInitHandler;


    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(mianThreadNum);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workThreadNum);

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws IOException {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ReadTimeoutHandler(timeOut));
                        pipeline.addLast(new WriteTimeoutHandler(timeOut));
                        pipeline.addLast("httpProtocoldecoder", new HttpRequestDecoder());
                        pipeline.addLast("httpProtocolEncoder", new HttpResponseEncoder());
                        pipeline.addLast("llmGateInit",llmGateInitHandler);
                        pipeline.addLast("llmGateLimited",llmGateLimitHandler);
                        pipeline.addLast("requstBodyAggegator", new HttpRequestBodyAggregator());
                        pipeline.addLast("urlRouter", new LLMGateUrlRouterHandler());
                        pipeline.addLast("paramValidation",new ParamValidationHandler());
                        pipeline.addLast("providerSelect",new ModelProviderSelectHandler());
                        pipeline.addLast("providerLimited", providerLimitHandler);
                        pipeline.addLast("modelLimited",modelLimitHandler);
                        pipeline.addLast("providerProxy",new ProviderProxyServerHandler());
                        pipeline.addLast("exceptionHandler", new LLMGateExceptionHandler());
                    }
                });

        ChannelFuture channelFuture = bootstrap.bind(port).syncUninterruptibly().addListener(future -> {
            LOGGER.info("LLMGate Server started on port {}", port);
        });
        channelFuture.channel().closeFuture().addListener(future -> {
            LOGGER.info("LLMGate Server Start Shutdown ............");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });
    }

}