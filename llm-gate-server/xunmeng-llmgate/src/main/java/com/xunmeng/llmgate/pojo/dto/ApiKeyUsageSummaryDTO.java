package com.xunmeng.llmgate.pojo.dto;

import com.xunmeng.llmgate.pojo.ApiKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyUsageSummaryDTO {

    private String apiKey;

    private Long totalInputTokens;
    private Long totalOutputTokens;

    private Integer totalCalls;
    private Integer errorCalls;

    private Double totalQuotaUsed;

    private ApiKey apiKeyInfo;
}
