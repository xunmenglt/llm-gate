package com.xunmeng.llmgate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunmeng.llmgate.pojo.dto.ProviderModelMappingDTO;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 大模型提供商-模型映射表 Mapper 接口
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface ProvuiderModelMappingMapper extends BaseMapper<ProvuiderModelMapping> {

    List<ProvuiderModelMapping> selectByProviderId(@Param("providerId") String providerId);

    List<ProviderModelMappingDTO> selectByConditions(@Param("providerName") String providerName,
                                                     @Param("modelName") String modelName);
}
