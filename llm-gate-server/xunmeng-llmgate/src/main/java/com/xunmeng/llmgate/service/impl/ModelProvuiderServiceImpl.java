package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.common.utils.StringUtils;
import com.xunmeng.llmgate.mapper.ModelProvuiderMapper;
import com.xunmeng.llmgate.mapper.ProvuiderModelMappingMapper;
import com.xunmeng.llmgate.pojo.dto.ModelInfoDTO;
import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.pojo.dto.ProviderWithModelsDTO;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import com.xunmeng.llmgate.service.IModelProvuiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 大模型提供商 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class ModelProvuiderServiceImpl extends ServiceImpl<ModelProvuiderMapper, ModelProvuider> implements IModelProvuiderService {
    @Autowired
    private ProvuiderModelMappingMapper mappingMapper;

    @Override
    public List<ModelProvuider> selectByConditions(String providerName, String type) {
        QueryWrapper<ModelProvuider> query = new QueryWrapper<>();

        if (StringUtils.isNotBlank(providerName)) {
            query.lambda().like(ModelProvuider::getProviderName, providerName);
        }
        if (StringUtils.isNotBlank(type)) {
            query.lambda().eq(ModelProvuider::getType, type);
        }

        query.lambda().orderByDesc(ModelProvuider::getCreateTime);
        return this.list(query);
    }


    @Override
    public ProviderWithModelsDTO getProviderWithModels(String providerId) {
        // 查询提供商信息
        ModelProvuider provider = this.getOne(
                new QueryWrapper<ModelProvuider>().eq("provider_id", providerId).last("LIMIT 1")
        );
        if (provider == null) {
            throw new RuntimeException("未找到对应提供商：" + providerId);
        }

        // 查询模型映射列表
        List<ProvuiderModelMapping> mappingList = mappingMapper.selectByProviderId(providerId);

        // 映射成模型信息
        List<ModelInfoDTO> modelInfoList = mappingList.stream().map(mapping -> {
            List<String> aliasList = StringUtils.isNotBlank(mapping.getModelNameAlias())
                    ? Arrays.asList(mapping.getModelNameAlias().split(","))
                    : Collections.emptyList();

            return new ModelInfoDTO(
                    mapping.getModelName(),
                    aliasList,
                    mapping.getMaxConcurrency()
            );
        }).collect(Collectors.toList());

        return new ProviderWithModelsDTO(provider, modelInfoList);
    }

    @Override
    public ModelProvuider getProviderById(String providerId) {
        // 查询提供商信息
        ModelProvuider provider = this.getOne(
                new QueryWrapper<ModelProvuider>().eq("provider_id", providerId).last("LIMIT 1")
        );

        return provider;
    }


}
