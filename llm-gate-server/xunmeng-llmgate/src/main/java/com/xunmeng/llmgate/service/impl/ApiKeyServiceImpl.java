package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.llmgate.enums.ApiKeyStatus;
import com.xunmeng.llmgate.enums.QuotaLimitType;
import com.xunmeng.llmgate.mapper.ApiKeyMapper;
import com.xunmeng.llmgate.mapper.LlmUsageStatsLogMapper;
import com.xunmeng.llmgate.pojo.ApiKey;
import com.xunmeng.llmgate.pojo.dto.ApiKeyUsageSummaryDTO;
import com.xunmeng.llmgate.pojo.dto.LlmUsageStatsAggregateDTO;
import com.xunmeng.llmgate.service.IApiKeyService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * api-key密钥表 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey> implements IApiKeyService {

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    @Autowired
    private LlmUsageStatsLogMapper logMapper;

    /**
     * 根据 API-KEY 查询对象（封装常用条件）
     * @param apiKey API-KEY 值
     * @return ApiKey 对象，若不存在则返回 null
     */
    private ApiKey getByKey(String apiKey) {
        return this.getOne(
                new QueryWrapper<ApiKey>()
                        .eq("`key`", apiKey)
                        .last("LIMIT 1")
        );
    }

    @Override
    public List<ApiKey> selectByConditions(String name, String userId, Integer status) {
        return this.apiKeyMapper.selectByConditions(name, userId, status);
    }

    @Override
    public List<ApiKey> selectInSelfByConditions(String name, String userId, Integer status) {
        return this.apiKeyMapper.selectInSelfByConditions(name, userId, status);
    }

    @Override
    public boolean changeQuotaByKey(String apiKey, double delta) {
        ApiKey key = getByKey(apiKey);
        if (key == null) return false;

        // 仅当为有限额度时修改
        if (key.getUnlimited() == QuotaLimitType.LIMITED.getCode()) {
            double newQuota = key.getQuota() + delta;
            if (newQuota < 0) return false; // 不允许为负
            key.setQuota(newQuota);
        }

        key.setUpdateTime(LocalDateTime.now());
        return this.updateById(key);
    }

    @Override
    public boolean isEnabled(String apiKey) {
        ApiKey key = getByKey(apiKey);
        return key != null && key.getStatus() == ApiKeyStatus.ENABLED.getCode();
    }

    @Override
    public boolean hasEnoughQuota(String apiKey, double required) {
        ApiKey key = getByKey(apiKey);
        if (key == null || key.getStatus() != ApiKeyStatus.ENABLED.getCode()) return false;

        // 无限额度时始终返回 true
        if (key.getUnlimited() == QuotaLimitType.UNLIMITED.getCode()) return true;

        return key.getQuota() >= required;
    }

    @Override
    public ApiKeyUsageSummaryDTO getUsageSummary(String apiKey) {
        // 查询 API-KEY 对象
        ApiKey key = getByKey(apiKey);
        if (key == null) {
            throw new RuntimeException("API-KEY 不存在：" + apiKey);
        }

        // 聚合查询日志数据
        LlmUsageStatsAggregateDTO stats = logMapper.aggregateStatsByKey(apiKey);

        return new ApiKeyUsageSummaryDTO(
                apiKey,
                stats.getTotalInputTokens(),
                stats.getTotalOutputTokens(),
                stats.getTotalCalls(),
                stats.getErrorCalls(),
                stats.getTotalQuotaUsed(),
                key
        );
    }

    @Override
    public ApiKey getInfoByKey(String apiKey) {
        return getByKey(apiKey);
    }

    @Override
    public boolean isExpired(String apiKey) {
        ApiKey key = getByKey(apiKey);
        if (ObjectUtils.isEmpty(key)){
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresTime = key.getExpiresTime();
        if (ObjectUtils.isNotEmpty(expiresTime) && expiresTime.isBefore(now)){
            return false;
        }
        return true;
    }
}
