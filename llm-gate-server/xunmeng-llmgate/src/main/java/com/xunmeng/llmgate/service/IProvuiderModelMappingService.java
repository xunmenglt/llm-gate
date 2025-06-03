package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import com.xunmeng.llmgate.pojo.dto.ProviderModelMappingDTO;

import java.util.List;

/**
 * <p>
 * 大模型提供商-模型映射表 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IProvuiderModelMappingService extends IService<ProvuiderModelMapping> {

    List<ProviderModelMappingDTO> selectByConditions(String providerName, String modelName);

}
