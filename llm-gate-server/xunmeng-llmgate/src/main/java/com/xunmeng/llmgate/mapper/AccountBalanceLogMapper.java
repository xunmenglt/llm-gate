package com.xunmeng.llmgate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunmeng.llmgate.pojo.AccountBalanceLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 账户资金变动日志 Mapper 接口
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface AccountBalanceLogMapper extends BaseMapper<AccountBalanceLog> {

    List<AccountBalanceLog> selectByConditions(@Param("userName") String userName,
                                               @Param("type") String type);
}
