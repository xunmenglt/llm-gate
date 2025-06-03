package com.xunmeng.llmgate.proxy.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xunmeng.common.constant.Constants;
import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.context.RequestContext;
import com.xunmeng.llmgate.proxy.error.BadRequestException;
import com.xunmeng.llmgate.proxy.error.IllegalHttpRequestException;
import com.xunmeng.llmgate.proxy.error.ProviderNotFoundException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;


@Slf4j
public class ProviderProxyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof FullHttpRequest)){
            throw new IllegalHttpRequestException("非法请求解析");
        }

        FullHttpRequest inboundRequest=(FullHttpRequest) msg;
        InvokingContext invokingContext=ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();
        RequestContext requestContext = ctx.channel().attr(ChannelAttributes.REQUEST_CONTEXT).get();
        ModelProvuider provider = invokingContext.getModelProvuider();
        String proxyUrl = provider.getProxyUrl();
        try {
            String body = requestContext.getBody();

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(body);
            if (rootNode instanceof ObjectNode) {
                // 修改 model 字段
                ((ObjectNode) rootNode).put("model", provider.getModelMapping().getModelName());
                // 重新序列化成字符串
                String newBody = objectMapper.writeValueAsString(rootNode);
                // 替换原来的内容
                byte[] newBodyBytes = newBody.getBytes(CharsetUtil.UTF_8);
                inboundRequest.content().clear().writeBytes(newBodyBytes);
                // 更新 Content-Length
                inboundRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, newBodyBytes.length);
                log.info("请求体model已路由替换为: {}", provider.getModelMapping().getModelName());
                inboundRequest.headers().set(Constants.AUTH_HEADER,Constants.TOKEN_PREFIX+provider.getApiKey());
            } else {
                throw new BadRequestException("请求体参数解析异常");
            }
        }catch (Exception e){
            throw new RuntimeException("代理解析异常");
        }

        if (StringUtils.isEmpty(proxyUrl)){
            throw new ProviderNotFoundException();
        }

        String host=getProxyHost(proxyUrl);
        Integer port=getProxyPort(proxyUrl);
        invokingContext.setProxyPort(port);
        invokingContext.setProxyHost(host);

        Bootstrap proxyBootstrap = new Bootstrap();
        proxyBootstrap.group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpRequestBodyAggregator());
                        // 添加内容处理器到代理连接的管道中
                        p.addLast(new ProviderProxyBackendHandler(ctx, inboundRequest.retain()));
                        p.addLast(new StreamRouterHandler());
                        p.addLast(new ProviderProxyExceptionHandler(ctx));
                    }
                });

        proxyBootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 连接成功，什么也不做，后续由 ProxyBackendHandler 处理
                ctx.channel().attr(ChannelAttributes.PROXY_CHANNEL_KEY).set(future.channel());
                future.channel().attr(ChannelAttributes.CLIENT_CHANNEL_KEY).set(ctx.channel());
            } else {
                throw new RuntimeException("服务商请求异常");
            }
        });
        super.channelRead(ctx,msg);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连接断开: " + ctx.channel().remoteAddress());
        Channel proxyChannel = ctx.channel().attr(ChannelAttributes.PROXY_CHANNEL_KEY).get();
        if (proxyChannel != null && proxyChannel.isActive()) {
            proxyChannel.close();
        }
        ctx.close();
    }


    public String getProxyHost(String proxyUrl) {
        if (proxyUrl == null || proxyUrl.isEmpty()) {
            return null;
        }
        try {
            URI uri = java.net.URI.create(proxyUrl);
            return uri.getHost();
        } catch (Exception e) {
            log.error("Failed to parse proxyUrl host: " + proxyUrl, e);
            return null;
        }
    }

    public Integer getProxyPort(String proxyUrl) {
        if (proxyUrl == null || proxyUrl.isEmpty()) {
            return null;
        }
        try {
            URI uri = java.net.URI.create(proxyUrl);
            int port = uri.getPort();
            if (port == -1) {
                // Return default ports based on scheme
                String scheme = uri.getScheme();
                if ("https".equalsIgnoreCase(scheme)) {
                    return 443;
                } else if ("http".equalsIgnoreCase(scheme)) {
                    return 80;
                }
            }
            return port;
        } catch (Exception e) {
            log.error("Failed to parse proxyUrl port: " + proxyUrl, e);
            return null;
        }
    }
}
