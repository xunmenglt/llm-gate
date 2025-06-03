package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.pojo.dto.ProviderWithModelsDTO;

import java.util.List;

/**
 * <p>
 * 大模型提供商 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IModelProvuiderService extends IService<ModelProvuider> {

    List<ModelProvuider> selectByConditions(String providerName, String type);

    ProviderWithModelsDTO getProviderWithModels(String providerId);

    ModelProvuider getProviderById(String providerId);


}
