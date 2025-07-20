package com.xunmeng.llmgate.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long outputTokens;
    private Long inputLen;
    private Long outputLen;
    private Double quota;
    private Integer error;
    private String errorType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
