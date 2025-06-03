package com.xunmeng.llmgate.pojo.dto;

import com.xunmeng.llmgate.pojo.ModelProvuider;
import lombok.Data;

import java.util.List;

@Data
public class ProviderWithModelsDTO {

    private ModelProvuider provider;

    private List<ModelInfoDTO> models;

    public ProviderWithModelsDTO(ModelProvuider provider, List<ModelInfoDTO> models) {
        this.provider = provider;
        this.models = models;
    }
}
