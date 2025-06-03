package com.xunmeng.llmgate.service.strategy;

import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import com.xunmeng.llmgate.service.IModelProvuiderService;
import com.xunmeng.llmgate.service.IProvuiderModelMappingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModelProviderSelector {

    private final IProvuiderModelMappingService mappingService;
    private final IModelProvuiderService providerService;

    /**
     * 根据请求模型名，选择一个可用模型服务商
     */
    public ModelProvuider selectProviderByModel(String requestModelName) {
        // 1. 获取所有映射记录
        List<ProvuiderModelMapping> mappings = mappingService.list();

        // 2. 匹配 model_name 或 model_name_alias
        List<ProvuiderModelMapping> matchedMappings = mappings.stream()
                .filter(m -> isMatch(requestModelName, m.getModelName(), m.getModelNameAlias()))
                .collect(Collectors.toList());

        if (matchedMappings.isEmpty()) {
            return null;
        }

        // 3. 从中随机选择一个
        ProvuiderModelMapping selectedMapping = matchedMappings.get(new Random().nextInt(matchedMappings.size()));

        // 4. 获取模型服务商信息
        ModelProvuider provider = providerService.getProviderById(selectedMapping.getProviderId());
        if (ObjectUtils.isNotEmpty(provider)) provider.setModelMapping(selectedMapping);
        return provider;
    }

    /**
     * 判断是否匹配模型名或别名
     */
    private boolean isMatch(String requestModel, String modelName, String aliasStr) {
        if (requestModel.equalsIgnoreCase(modelName)) return true;
        if (aliasStr != null) {
            String[] aliases = aliasStr.split(",");
            for (String alias : aliases) {
                if (requestModel.equalsIgnoreCase(alias.trim())) return true;
            }
        }
        return false;
    }
}
