package com.xunmeng.llmgate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunmeng.llmgate.pojo.Multiplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 倍率表 Mapper 接口
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface MultiplierMapper extends BaseMapper<Multiplier> {

    /**
     * 分页条件查询倍率列表（支持模型名称模糊匹配）
     * @param modelName 模型名称（可空）
     * @return 分页列表（由 startPage 生效）
     */
    List<Multiplier> selectByModelName(@Param("modelName") String modelName);
}

