package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.error.ModelProviderServerException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import java.net.URI;


@Slf4j
public class ProviderProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final ChannelHandlerContext clientCtx;

    private final FullHttpRequest clientRequest;

    public ProviderProxyBackendHandler(ChannelHandlerContext clientCtx, FullHttpRequest clientRequest) {
        this.clientCtx = clientCtx;
        this.clientRequest = clientRequest;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            InvokingContext invokingContext = this.clientCtx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();
            ModelProvuider modelProvuider = invokingContext.getModelProvuider();
            String proxyBaseUrl=modelProvuider.getProxyUrl();
            SSLEngine engine=null;
            if (proxyBaseUrl.startsWith("https")){
                engine = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build()
                        .newEngine(ctx.channel().alloc(), invokingContext.getProxyHost(), invokingContext.getProxyPort());
            }

            URI targetUri = new URI(proxyBaseUrl + invokingContext.getUri());

            if (engine!=null){
                ctx.pipeline().addFirst(new SslHandler(engine));
            }

            // 复制原请求
            FullHttpRequest outboundRequest = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1,
                    clientRequest.method(),
                    targetUri.getRawPath(),
                    clientRequest.content()
            );
            outboundRequest.headers().setAll(clientRequest.headers());
            outboundRequest.headers().set(HttpHeaderNames.HOST, targetUri.getHost());
            ctx.writeAndFlush(outboundRequest);
        } catch (Exception e) {
            clientCtx.fireExceptionCaught(new ModelProviderServerException("模型服务商请求失败"));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext proxyCtx, Object proxyMsg) {
        try {
            if (proxyMsg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) proxyMsg;
                HttpResponse copiedResponse = new DefaultHttpResponse(
                        response.protocolVersion(),
                        response.status()
                );

                copiedResponse.headers().set(response.headers());
                clientCtx.writeAndFlush(copiedResponse);
            }

            if (proxyMsg instanceof HttpContent) {
                HttpContent content = ((HttpContent) proxyMsg).copy();
                clientCtx.writeAndFlush(content);
            }

            proxyCtx.fireChannelRead(proxyMsg);

            if (proxyMsg instanceof LastHttpContent) {
                clientCtx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                clientCtx.close();
            }
        } catch (Exception e) {
            clientCtx.fireExceptionCaught(new ModelProviderServerException("模型服务商请求失败"));
        }
    }



    @Override
    public void channelInactive(ChannelHandlerContext proxyCtx) throws Exception {
        super.channelInactive(proxyCtx);
        clientCtx.close();
    }
}
