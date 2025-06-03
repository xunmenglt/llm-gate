package com.xunmeng.llmgate.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProviderModelMappingDTO {
    private Long id;
    private String providerId;
    private String providerName;
    private String modelName;
    private String modelNameAlias;
    private Integer maxConcurrency;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}