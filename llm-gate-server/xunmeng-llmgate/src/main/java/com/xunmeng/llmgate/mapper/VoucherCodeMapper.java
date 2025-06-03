package com.xunmeng.llmgate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunmeng.llmgate.pojo.VoucherCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 兑换码 Mapper 接口
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface VoucherCodeMapper extends BaseMapper<VoucherCode> {

    /**
     * 分页查询兑换码（支持条件筛选）
     * @param creator 创建人
     * @param consumer 消费人
     * @param status 状态（-1未使用、1已使用、0禁用）
     */
    List<VoucherCode> selectByConditions(@Param("creator") String creator,
                                         @Param("consumer") String consumer,
                                         @Param("status") Integer status);
}
