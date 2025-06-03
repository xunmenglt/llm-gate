package com.xunmeng.llmgate.proxy.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.context.RequestContext;
import com.xunmeng.llmgate.proxy.error.BadRequestException;
import com.xunmeng.llmgate.proxy.error.LLMGateNotImplementedException;
import com.xunmeng.llmgate.proxy.error.ModelNotFoundException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
public class ParamValidationHandler extends ChannelInboundHandlerAdapter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestContext requestContext = ctx.channel().attr(ChannelAttributes.REQUEST_CONTEXT).get();
        InvokingContext invokingContext = ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).get();

        if (requestContext.getMethod() != HttpMethod.POST) {
            throw new LLMGateNotImplementedException("仅支持 POST 方法");
        }

        String contentType = requestContext.getHeaders().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType == null || !contentType.contains("application/json")) {
            throw new BadRequestException("Content-Type 必须是 application/json");
        }

        String body = requestContext.getBody();
        if (ObjectUtils.isEmpty(body)) {
            throw new BadRequestException("请求体不能为空");
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(body);
            if (!jsonNode.has("model") || jsonNode.get("model").asText().isEmpty()) {
                throw new ModelNotFoundException("缺少 model 参数");
            }

            invokingContext.setModelName(jsonNode.get("model").asText());
            ctx.fireChannelRead(msg); // 只调用一次
        } catch (Exception e) {
            log.warn("请求体 JSON 解析失败: {}", body, e);
            throw new BadRequestException("请求体不是合法 JSON 格式");
        }
    }
}

