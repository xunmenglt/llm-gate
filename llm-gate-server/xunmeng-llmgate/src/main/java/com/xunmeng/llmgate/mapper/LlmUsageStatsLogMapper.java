package com.xunmeng.llmgate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunmeng.llmgate.pojo.LlmUsageStatsLog;
import com.xunmeng.llmgate.pojo.dto.LlmUsageStatsAggregateDTO;
import com.xunmeng.llmgate.pojo.dto.LlmUsageStatsLogDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 模型调用日志 Mapper 接口
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface LlmUsageStatsLogMapper extends BaseMapper<LlmUsageStatsLog> {

    List<LlmUsageStatsLogDTO> selectByConditions(@Param("providerName") String providerName,
                                                 @Param("modelName") String modelName,
                                                 @Param("userName") String userName);

    List<LlmUsageStatsLogDTO> selectInSelfByConditions(@Param("providerName") String providerName,
                                                       @Param("modelName") String modelName,
                                                       @Param("userName") String userName);

    LlmUsageStatsAggregateDTO aggregateStatsByKey(@Param("apiKey") String apiKey);
}
