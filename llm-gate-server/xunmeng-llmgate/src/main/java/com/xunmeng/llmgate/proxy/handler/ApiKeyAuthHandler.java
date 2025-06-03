package com.xunmeng.llmgate.proxy.handler;

import com.xunmeng.common.constant.Constants;
import com.xunmeng.common.utils.spring.SpringUtils;
import com.xunmeng.llmgate.enums.ApiKeyStatus;
import com.xunmeng.llmgate.enums.QuotaLimitType;
import com.xunmeng.llmgate.pojo.Account;
import com.xunmeng.llmgate.pojo.ApiKey;
import com.xunmeng.llmgate.proxy.attributes.ChannelAttributes;
import com.xunmeng.llmgate.proxy.context.InvokingContext;
import com.xunmeng.llmgate.proxy.context.LLMGateContext;
import com.xunmeng.llmgate.proxy.context.RequestContext;
import com.xunmeng.llmgate.proxy.error.LLMGateAuthorizationException;
import com.xunmeng.llmgate.service.IAccountService;
import com.xunmeng.llmgate.service.IApiKeyService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Slf4j
public class ApiKeyAuthHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestContext requestContext=ctx.channel().attr(ChannelAttributes.REQUEST_CONTEXT).get();
        LLMGateContext llmGateContext = ctx.channel().attr(ChannelAttributes.LLMGATE_CONTEXT).get();

        String authHeader = requestContext.getHeaders().get(Constants.AUTH_HEADER);

        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            throw new LLMGateAuthorizationException("缺少或非法的 Authorization 头");
        }

        String apiKey = authHeader.substring(Constants.TOKEN_PREFIX.length()).trim();
        if (StringUtils.isEmpty(apiKey)) {
            throw new LLMGateAuthorizationException("API-KEY 不能为空");
        }

        IApiKeyService apiKeyService = SpringUtils.getBean(IApiKeyService.class);
        ApiKey key = apiKeyService.getInfoByKey(apiKey);
        if (key == null) {
            throw new LLMGateAuthorizationException("API-KEY 无效");
        }

        if (ApiKeyStatus.DISABLED.getCode()==key.getStatus()) {
            throw new LLMGateAuthorizationException("API-KEY 已被禁用");
        }

        if (ObjectUtils.isNotEmpty(key.getExpiresTime()) && key.getExpiresTime().isBefore(LocalDateTime.now())) {
            throw new LLMGateAuthorizationException("API-KEY 已过期");
        }

        if (QuotaLimitType.LIMITED.getCode()==key.getUnlimited() && key.getQuota() <= 0) {
            throw new LLMGateAuthorizationException("API-KEY 余额不足");
        }

        IAccountService accountService = SpringUtils.getBean(IAccountService.class);
        Account account = accountService.getUserAccount(key.getUserName());

        if (ObjectUtils.isEmpty(account)){
            throw new LLMGateAuthorizationException("用户账户信息不存在");
        }

        if (account.getBalance()<=0){
            throw new LLMGateAuthorizationException("用户余额不足");
        }
        String uri=ObjectUtils.isEmpty(llmGateContext.getPrefix())?requestContext.getPath():requestContext.getPath().replace(llmGateContext.getPrefix(),"");
        InvokingContext invokingContext = new InvokingContext();
        invokingContext.setApiKey(key);
        invokingContext.setUserId(key.getUserName());
        invokingContext.setAccount(account);
        invokingContext.setUri(uri);
        ctx.channel().attr(ChannelAttributes.INVOKING_CONTEXT).set(invokingContext);
        super.channelRead(ctx,msg);
    }


}
