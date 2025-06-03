package com.xunmeng.llmgate.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LlmUsageStatsLogDTO {
    private String logId;
    private String providerId;
    private String providerName;
    private String modelName;
    private String apiKey;
    private String userName;
    private Long inputTokens;
    private Long ouputTokens;
    private Double quota;
    private Integer error;
    private String errorType;
    private LocalDateTime createTime;
}
