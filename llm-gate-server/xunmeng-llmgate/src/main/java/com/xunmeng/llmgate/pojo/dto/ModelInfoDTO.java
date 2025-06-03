package com.xunmeng.llmgate.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ModelInfoDTO {

    private String modelName;

    private List<String> alias;

    private int maxConcurrency;
}
