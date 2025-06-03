package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.RequestContext;
import com.xunmeng.llmgate.proxy.error.IllegalHttpRequestException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpRequestBodyAggregator extends ChannelInboundHandlerAdapter {

    private HttpRequest request;
    private ByteBuf bodyBuffer;
    private boolean readingRequest = false;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean handled = false;

        try {
            if (msg instanceof HttpRequest) {
                this.request = (HttpRequest) msg;
                this.bodyBuffer = Unpooled.buffer();
                this.readingRequest = true;
                handled = true;
            } else if (readingRequest && msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                bodyBuffer.writeBytes(content.content());

                if (msg instanceof LastHttpContent) {
                    FullHttpRequest fullRequest = buildFullHttpRequest();
                    RequestContext requestContext = createRequestContext(ctx, fullRequest);
                    ctx.channel().attr(ChannelAttributes.REQUEST_CONTEXT).set(requestContext);
                    ctx.fireChannelRead(fullRequest); // 放行完整请求
                    reset(); // 清理状态
                }
                handled = true;
            } else if (msg instanceof HttpResponse || msg instanceof HttpContent) {
                super.channelRead(ctx,msg);
            } else {
                throw new IllegalHttpRequestException("请求头异常");
            }
        } finally {
            if (!handled) {
                ReferenceCountUtil.release(msg); // 只释放未处理的消息
            }
        }
    }

    private FullHttpRequest buildFullHttpRequest() {
        FullHttpRequest fullRequest = new DefaultFullHttpRequest(
                request.protocolVersion(),
                request.method(),
                request.uri(),
                bodyBuffer
        );
        fullRequest.headers().setAll(request.headers());
        fullRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, bodyBuffer.readableBytes());
        return fullRequest;
    }

    private void reset() {
        request = null;
        bodyBuffer = null;
        readingRequest = false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        reset();
        super.channelInactive(ctx);
    }

    private RequestContext createRequestContext(ChannelHandlerContext ctx,FullHttpRequest request){
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String path = decoder.path();
        Map<String, List<String>> queryParams = decoder.parameters();

        String clientIp = ctx.channel().remoteAddress().toString(); // 可再优化为实际 IP
        String body = request.content().toString(StandardCharsets.UTF_8);

        RequestContext context = new RequestContext(
                path,
                request.method(),
                clientIp,
                request.headers(),
                queryParams,
                body
        );
        return context;
    }
}
