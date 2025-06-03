package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.common.utils.uuid.UUID;
import com.xunmeng.llmgate.enums.BalanceChangeType;
import com.xunmeng.llmgate.mapper.AccountBalanceLogMapper;
import com.xunmeng.llmgate.pojo.AccountBalanceLog;
import com.xunmeng.llmgate.service.IAccountBalanceLogService;
import com.xunmeng.llmgate.utils.IdAndCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 账户资金变动日志 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class AccountBalanceLogServiceImpl extends ServiceImpl<AccountBalanceLogMapper, AccountBalanceLog> implements IAccountBalanceLogService {

    @Autowired
    private AccountBalanceLogMapper accountBalanceLogMapper;

    public boolean doLog(String userId, double delta, BalanceChangeType type, String voucherCode, String modelCallLogId) {
        AccountBalanceLog log = new AccountBalanceLog();
        log.setLogId(IdAndCodeGenerator.generateLogId());
        log.setUserName(userId);
        log.setDeltaAmount(delta);
        log.setType(type.getCode());
        log.setVoucherCode(voucherCode);
        log.setModelCallLogId(modelCallLogId);
        log.setCreateTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        return this.save(log);
    }

    @Override
    public boolean doConsumeVoucherCodeLog(String userId, double delta, String voucherCode) {
        return doLog(
                userId,
                delta,
                BalanceChangeType.VOUCHER_CODE_USE,
                voucherCode,
                null
        );
    }

    @Override
    public boolean doModelCallLog(String userId, double delta, String modelCallLogId) {
        return doLog(
                userId,
                delta,
                BalanceChangeType.MODEL_CALL,
                null,
                modelCallLogId
                );
    }

    @Override
    public List<AccountBalanceLog> getLogByConditions(String userName, String type) {

        List<AccountBalanceLog> list = accountBalanceLogMapper.selectByConditions(userName, type);
        return list;
    }


}
