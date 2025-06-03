package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.llmgate.enums.VoucherStatus;
import com.xunmeng.llmgate.mapper.AccountMapper;
import com.xunmeng.llmgate.pojo.Account;
import com.xunmeng.llmgate.pojo.AccountBalanceLog;
import com.xunmeng.llmgate.pojo.VoucherCode;
import com.xunmeng.llmgate.service.IAccountBalanceLogService;
import com.xunmeng.llmgate.service.IAccountService;
import com.xunmeng.llmgate.service.IVoucherCodeService;
import com.xunmeng.llmgate.utils.IdAndCodeGenerator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 账户表 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {


    @Autowired
    private IVoucherCodeService voucherCodeService;

    @Autowired
    private IAccountBalanceLogService accountBalanceLogService;


    private Account getOneAccount(String userId){
        Account account = this.getOne(new QueryWrapper<Account>()
                .eq("user_name", userId)
                .last("LIMIT 1"));
        return account;
    }

    @Override
    public Account getUserAccount(String userId) {
        Account account = getOneAccount(userId);
        if (ObjectUtils.isEmpty(account)){
             account=createUserAccount(userId);
         }
         return  account;
    }

    @Override
    public Account createUserAccount(String userId) {
        Account account = getOneAccount(userId);
        if (ObjectUtils.isEmpty(account)){
            account = new Account();
            account.setBalance(0.0);
            account.setUserName(userId);
            this.save(account);
        }
        return  account;
    }


    @Override
    public boolean hasEnoughBalance(String userId) {
        Account userAccount = getUserAccount(userId);
        if (userAccount.getBalance()>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean changeBalance(String userId, double delta) {
        Account account = getUserAccount(userId);
        if (ObjectUtils.isEmpty(account)){
            return false;
        }
        double newBalance = account.getBalance() + delta;
        account.setBalance(newBalance);
        account.setUpdateTime(LocalDateTime.now());
        return this.updateById(account);
    }

    @Override
    @Transactional
    public boolean redeemVoucherCode(String userId, String voucherCode) {
        VoucherCode code = voucherCodeService.consume(userId, voucherCode);
        if (ObjectUtils.isEmpty(code)) {
            throw new RuntimeException("兑换码无效或已使用");
        }

        // 增加余额
        Account account = getUserAccount(userId);

        if (account == null) throw new RuntimeException("账户不存在");

        boolean flag = changeBalance(userId, code.getQuota());

        if (!flag) {
            throw new RuntimeException("修改账户信息异常");
        }
        // 写入日志
        accountBalanceLogService.doConsumeVoucherCodeLog(userId, code.getQuota(), voucherCode);
        return true;
    }
}
