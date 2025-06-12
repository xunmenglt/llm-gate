package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.llmgate.enums.LlmErrorType;
import com.xunmeng.llmgate.enums.QuotaLimitType;
import com.xunmeng.llmgate.mapper.LlmUsageStatsLogMapper;
import com.xunmeng.llmgate.pojo.Account;
import com.xunmeng.llmgate.pojo.ApiKey;
import com.xunmeng.llmgate.pojo.LlmUsageStatsLog;
import com.xunmeng.llmgate.pojo.Multiplier;
import com.xunmeng.llmgate.pojo.dto.LlmUsageStatsLogDTO;
import com.xunmeng.llmgate.service.*;
import com.xunmeng.llmgate.utils.IdAndCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 模型调用日志 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class LlmUsageStatsLogServiceImpl extends ServiceImpl<LlmUsageStatsLogMapper, LlmUsageStatsLog> implements ILlmUsageStatsLogService {
    @Autowired
    private IAccountService accountService;

    @Autowired
    private IApiKeyService apiKeyService;

    @Autowired
    private IAccountBalanceLogService accountBalanceLogService;

    @Autowired
    private IMultiplierService multiplierService;

    @Override
    public List<LlmUsageStatsLogDTO> selectByConditions(String providerName, String modelName, String userName) {
        return this.baseMapper.selectByConditions(providerName, modelName, userName);
    }

    @Override
    public List<LlmUsageStatsLogDTO> selectInSelfByConditions(String providerName, String modelName, String userId) {
        return this.baseMapper.selectInSelfByConditions(providerName, modelName, userId);
    }

    @Override
    public boolean doLog(String logId,String providerId, String modelName, String apiKey,
                         Long inputTokens, Long outputTokens, double quota,
                         boolean isError, LlmErrorType errorType) {
        LlmUsageStatsLog log = new LlmUsageStatsLog();
        log.setLogId(logId);
        log.setProviderId(providerId);
        log.setModelName(modelName);
        log.setApiKey(apiKey);
        log.setInputTokens(inputTokens != null ? inputTokens : 0L);
        log.setOuputTokens(outputTokens != null ? outputTokens : 0L);
        log.setQuota(quota);
        log.setError(isError ? 1 : 0);
        log.setErrorType(errorType != null ? errorType.getCode() : LlmErrorType.UNKNOWN.getCode());
        log.setCreateTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        return this.save(log);
    }


    @Override
    @Transactional
    public boolean doSuccessLog(String providerId, String modelName, String apiKey,
                                Long inputTokens, Long outputTokens, double quota) {
        double price=multiplierService.calculatePrice(modelName,inputTokens,outputTokens);
        ApiKey key = apiKeyService.getInfoByKey(apiKey);
        String userId=key.getUserName();
        Account account=accountService.getUserAccount(userId);
        if (account==null) {
            throw  new RuntimeException("账户不存在");
        }
        boolean a=accountService.changeBalance(userId,-price);
        // 减少apikey的余额
        if (key.getUnlimited()== QuotaLimitType.LIMITED.getCode()){
            apiKeyService.changeQuotaByKey(apiKey,-price);
        }
        if (!a){
            throw  new RuntimeException("修改账户信息异常");
        }
        // 写入日志
        String logId = IdAndCodeGenerator.generateLogId();
        accountBalanceLogService.doModelCallLog(userId,-price,logId);
        boolean b = doLog(logId,providerId, modelName, apiKey, inputTokens, outputTokens, price, false, null);
        return b;
    }

    @Override
    public boolean doErrorLog(String providerId, String modelName, String apiKey,
                              LlmErrorType errorType) {
        String logId = IdAndCodeGenerator.generateLogId();

        return doLog(logId,providerId, modelName, apiKey, 0L, 0L, 0.0, true, errorType);
    }
}
