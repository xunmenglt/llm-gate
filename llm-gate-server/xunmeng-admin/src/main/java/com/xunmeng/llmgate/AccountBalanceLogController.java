package com.xunmeng.llmgate;


import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.llmgate.pojo.AccountBalanceLog;
import com.xunmeng.llmgate.service.IAccountBalanceLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 账户资金变动日志 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/balanceLog")
@Api(tags = "账户资金日志管理")
public class AccountBalanceLogController extends BaseController {

    @Autowired
    private IAccountBalanceLogService accountBalanceLogService;

    @ApiOperation("分页查询资金变动日志")
    @PreAuthorize("@ss.hasPermi('account:detail')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "userName", required = false) String userName,
                              @RequestParam(value = "type", required = false) String type) {
        startPage();
        List<AccountBalanceLog> list = accountBalanceLogService.getLogByConditions(userName, type);
        return getDataTable(list);
    }
}