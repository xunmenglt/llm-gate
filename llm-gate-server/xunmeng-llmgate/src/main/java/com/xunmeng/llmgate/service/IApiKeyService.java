package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.pojo.ApiKey;
import com.xunmeng.llmgate.pojo.dto.ApiKeyUsageSummaryDTO;

import java.util.List;

/**
 * <p>
 * api-key密钥表 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IApiKeyService extends IService<ApiKey> {

    List<ApiKey> selectByConditions(String name, String userId, Integer status);

    List<ApiKey> selectInSelfByConditions(String name, String userId, Integer status);


    /**
     * 根据 API-KEY 修改余额（增加或减少）
     * @param apiKey API-KEY 值
     * @param delta 金额变动值（正数为增加，负数为扣减）
     * @return 是否成功
     */
    boolean changeQuotaByKey(String apiKey, double delta);

    /**
     * 判断 API-KEY 是否启用（状态为 1）
     * @param apiKey API-KEY 值
     * @return true = 已启用，false = 已禁用或不存在
     */
    boolean isEnabled(String apiKey);

    /**
     * 判断余额是否充足（或设置为无限额度）
     * @param apiKey API-KEY 值
     * @param required 所需扣减额度
     * @return true = 充足，false = 不足或已禁用
     */
    boolean hasEnoughQuota(String apiKey, double required);

    /**
     * 查询指定 API-KEY 的使用汇总信息
     * @param apiKey API 密钥
     */
    ApiKeyUsageSummaryDTO getUsageSummary(String apiKey);


    ApiKey getInfoByKey(String apiKey);

    boolean isExpired(String apiKey);

}
