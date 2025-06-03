package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.pojo.Account;

/**
 * <p>
 * 账户表 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IAccountService extends IService<Account> {

    /**
     * 获取用户账户信息
     * @param userId
     * @return
     */
    Account getUserAccount(String userId);

    Account createUserAccount(String userId);

    /**
     * 判断是否有充足的余额
     * @param userId
     * @return
     */
    boolean hasEnoughBalance(String userId);


    /**
     * 余额变动
     * @param userId
     * @param delta
     * @return
     */
    boolean changeBalance(String userId,double delta);


    /**
     * 兑换码兑换工作
     * @param userId
     * @param voucherCode
     * @return
     */
    boolean redeemVoucherCode(String userId,String voucherCode);

}
