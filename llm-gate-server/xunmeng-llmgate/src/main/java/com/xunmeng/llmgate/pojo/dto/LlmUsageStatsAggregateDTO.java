package com.xunmeng.llmgate.pojo.dto;

import lombok.Data;

@Data
public class LlmUsageStatsAggregateDTO {
    private Long totalInputTokens;
    private Long totalOutputTokens;
    private Integer totalCalls;
    private Integer errorCalls;
    private Double totalQuotaUsed;
}
