package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.enums.BalanceChangeType;
import com.xunmeng.llmgate.pojo.AccountBalanceLog;

import java.util.List;

/**
 * <p>
 * 账户资金变动日志 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IAccountBalanceLogService extends IService<AccountBalanceLog> {

    /**
     * 写入一条资金变动日志
     *
     * @param userId 用户ID
     * @param delta 金额变动值（正为增加，负为扣减）
     * @param type 变动类型（建议使用枚举 BalanceChangeType）
     * @param voucherCode 使用的兑换码（可空）
     * @param modelCallLogId 模型调用日志ID（可空）
     * @return 是否写入成功
     */
    boolean doLog(String userId, double delta, BalanceChangeType type, String voucherCode, String modelCallLogId);


    /**
     * 兑换码消费日志
     * @param userId
     * @param delta
     * @param voucherCode
     * @return
     */
    boolean doConsumeVoucherCodeLog(String userId, double delta, String voucherCode);

    /**
     * 模型调用日志
     * @param userId
     * @param delta
     * @param modelCallLogId
     * @return
     */
    boolean doModelCallLog(String userId, double delta, String modelCallLogId);

    List<AccountBalanceLog> getLogByConditions(String userName, String type);
}
