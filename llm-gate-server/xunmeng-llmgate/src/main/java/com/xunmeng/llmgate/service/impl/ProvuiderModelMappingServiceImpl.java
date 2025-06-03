package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.llmgate.mapper.ProvuiderModelMappingMapper;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import com.xunmeng.llmgate.pojo.dto.ProviderModelMappingDTO;
import com.xunmeng.llmgate.service.IProvuiderModelMappingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 大模型提供商-模型映射表 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class ProvuiderModelMappingServiceImpl extends ServiceImpl<ProvuiderModelMappingMapper, ProvuiderModelMapping> implements IProvuiderModelMappingService {

    @Override
    public List<ProviderModelMappingDTO> selectByConditions(String providerName, String modelName) {

        return baseMapper.selectByConditions(providerName, modelName);

    }
}
