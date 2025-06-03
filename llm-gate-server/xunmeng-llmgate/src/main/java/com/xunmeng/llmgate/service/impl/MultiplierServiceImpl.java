package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.llmgate.mapper.MultiplierMapper;
import com.xunmeng.llmgate.pojo.Multiplier;
import com.xunmeng.llmgate.service.IMultiplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 倍率表 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class MultiplierServiceImpl extends ServiceImpl<MultiplierMapper, Multiplier> implements IMultiplierService {


    @Autowired
    private MultiplierMapper multiplierMapper;
    private static final double DEFAULT_RATE = 1;

    @Override
    public boolean saveOrUpdateMultiplier(Multiplier multiplier) {
        // 判断是否已有相同模型名的记录
        Multiplier existing = getByModelName(multiplier.getModelName());
        LocalDateTime now = LocalDateTime.now();

        if (existing != null) {
            multiplier.setId(existing.getId());
            multiplier.setUpdateTime(now);
        } else {
            multiplier.setCreateTime(now);
            multiplier.setUpdateTime(now);
        }

        return this.saveOrUpdate(multiplier);
    }

    @Override
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }

    @Override
    public Multiplier getByModelName(String modelName) {
        Multiplier multiplier = this.getOne(
                new QueryWrapper<Multiplier>()
                        .eq("model_name", modelName)
                        .last("LIMIT 1")
        );
        if (multiplier == null) {
            multiplier=getDefaultMultiplier();
        }
        return multiplier;
    }

    @Override
    public Multiplier getDefaultMultiplier() {
        Multiplier defaultMultiplier = new Multiplier();
        defaultMultiplier.setRate(DEFAULT_RATE);
        defaultMultiplier.setInputPrivice(0.0);
        defaultMultiplier.setOutputPrivice(0.0);
        defaultMultiplier.setModelName("default");
        return defaultMultiplier;
    }

    @Override
    public double calculatePrice(String modelName, long inputTokens, long outputTokens) {
        Multiplier multiplier = this.getByModelName(modelName); // 自动 fallback 默认倍率

        double inputPricePerThousand = multiplier.getInputPrivice();    // 每1000输入token价格（单位：元）
        double outputPricePerThousand = multiplier.getOutputPrivice();
        double rate = multiplier.getRate();                 // 倍率

        double totalInputCost = (inputTokens / 1000.0) * inputPricePerThousand;
        double totalOutputCost = (outputTokens / 1000.0) * outputPricePerThousand;

        return (totalInputCost + totalOutputCost) * rate;
    }

    @Override
    public List<Multiplier> selectByModelName(String modelName) {
        return multiplierMapper.selectByModelName(modelName);
    }

}

