package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.common.constant.Constants;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.LLMGateContext;
import com.xunmeng.llmgate.proxy.context.RequestContext;
import com.xunmeng.llmgate.proxy.error.IllegalHttpRequestException;
import com.xunmeng.llmgate.proxy.error.LLMGateAuthorizationException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class LLMGateUrlRouterHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RequestContext requestContext = ctx.channel().attr(ChannelAttributes.REQUEST_CONTEXT).get();

        if (ObjectUtils.isEmpty(requestContext)){
            throw new IllegalHttpRequestException("非法请求");
        }

        String path = requestContext.getPath();

        LLMGateContext llmGateContext = ctx.channel().attr(ChannelAttributes.LLMGATE_CONTEXT).get();

        // 根据路径决定替换后续处理逻辑
        if (StringUtils.isNotEmpty(llmGateContext.getPrefix()) && !path.startsWith(llmGateContext.getPrefix())) {
            throw new IllegalHttpRequestException("非法请求");
        }

        HttpHeaders headers = requestContext.getHeaders();
        if (ObjectUtils.isNotEmpty(headers)){
            String auth_key = headers.get(Constants.AUTH_HEADER);
            if (ObjectUtils.isNotEmpty(auth_key) && auth_key.startsWith(Constants.TOKEN_PREFIX)){
                ctx.pipeline().replace(this, "chatHandler", new ApiKeyAuthHandler());
                // 让新 handler 继续处理这个请求
                ctx.fireChannelRead(msg);
                return;
            }
            throw new LLMGateAuthorizationException("密钥KEY异常");
        }else {
            throw new LLMGateAuthorizationException("认证异常，请携带密钥KEY");
        }
    }
}
