package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.pojo.Multiplier;

import java.util.List;

/**
 * <p>
 * 倍率表 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IMultiplierService extends IService<Multiplier> {

    /**
     * 新增或更新倍率信息（按模型名唯一）
     */
    boolean saveOrUpdateMultiplier(Multiplier multiplier);

    /**
     * 根据ID删除倍率信息
     */
    boolean deleteById(Long id);

    /**
     * 根据模型名称查找倍率信息
     */
    Multiplier getByModelName(String modelName);

    /**
     * 获取默认倍率信息（例如：默认倍率 = 1.33）
     */
    Multiplier getDefaultMultiplier();

    /**
     * 根据输入输出 token 数量、模型名称计算价格（含倍率）
     *
     * @param modelName 模型名称
     * @param inputTokens 输入 token 数量
     * @param outputTokens 输出 token 数量
     * @return 总价格（单位：元）
     */
    double calculatePrice(String modelName, long inputTokens, long outputTokens);

    List<Multiplier> selectByModelName(String modelName);
}
