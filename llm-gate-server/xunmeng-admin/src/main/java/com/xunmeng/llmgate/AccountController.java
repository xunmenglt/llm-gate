package com.xunmeng.llmgate;


import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.llmgate.pojo.Account;
import com.xunmeng.llmgate.service.IAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 账户表 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/account")
@Api(tags = "账户管理")
public class AccountController extends BaseController {

    @Autowired
    private IAccountService accountService;

    @ApiOperation(value = "获取当前用户余额")
    @PreAuthorize("@ss.hasPermi('account:detail')")
    @GetMapping("/balance")
    public AjaxResult getBalance() {
        String userId = getUserId();
        Account account = accountService.getUserAccount(userId);
        if (account == null) {
            return AjaxResult.error("获取余额失败");
        }
        return AjaxResult.success(account.getBalance());
    }

}
