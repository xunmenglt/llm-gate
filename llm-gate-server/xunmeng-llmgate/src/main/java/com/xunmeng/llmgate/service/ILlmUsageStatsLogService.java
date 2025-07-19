package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.enums.LlmErrorType;
import com.xunmeng.llmgate.pojo.LlmUsageStatsLog;
import com.xunmeng.llmgate.pojo.dto.LlmUsageStatsLogDTO;

import java.util.List;

/**
 * <p>
 * 模型调用日志 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface ILlmUsageStatsLogService extends IService<LlmUsageStatsLog> {

    List<LlmUsageStatsLogDTO> selectByConditions(String providerName, String modelName, String userName);

    List<LlmUsageStatsLogDTO> selectInSelfByConditions(String providerName, String modelName, String userId);

    LlmUsageStatsLogDTO selectByRequestId(String requestId);

    boolean doLog(String logId,String providerId, String modeName, String api_key, Long input_tokens, Long output_tokens, double quota, boolean isError, LlmErrorType errorType, String requestId,Long inputLen,Long outputLen);

    boolean doSuccessLog(String providerId, String modeName, String apiKey, Long inputTokens, Long outputTokens, double quota,String requestId,Long inputLen,Long outputLen);
    boolean doErrorLog(String providerId, String modeName, String apiKey,LlmErrorType errorType,String requestId,Long inputLen,Long outputLen);
}
