package com.xunmeng.llmgate.proxy.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.llmgate.proxy.error.BaseLLMGateException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class ProviderProxyExceptionHandler extends ChannelInboundHandlerAdapter {

    private final ChannelHandlerContext clientCtx;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ProviderProxyExceptionHandler(ChannelHandlerContext clientCtx){
        this.clientCtx=clientCtx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        AjaxResult result;
        HttpResponseStatus status;

        if (cause instanceof BaseLLMGateException) {
            BaseLLMGateException ex = (BaseLLMGateException) cause;
            result = AjaxResult.error(ex.getCode(), ex.getMessage());
            status = HttpResponseStatus.BAD_REQUEST;
        } else {
            // 未知异常兜底处理
            result = AjaxResult.error(50000, "内部服务器错误");
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }

        try {
            String json = objectMapper.writeValueAsString(result);
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    status,
                    Unpooled.copiedBuffer(json, CharsetUtil.UTF_8)
            );
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            clientCtx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        } catch (Exception e) {
            FullHttpResponse fallback = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.copiedBuffer("{\"code\":50000,\"msg\":\"服务器错误\"}", CharsetUtil.UTF_8)
            );
            fallback.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            fallback.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, fallback.content().readableBytes());
            clientCtx.writeAndFlush(fallback).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }
}
