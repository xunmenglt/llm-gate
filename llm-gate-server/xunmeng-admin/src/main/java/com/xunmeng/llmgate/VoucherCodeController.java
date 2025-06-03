package com.xunmeng.llmgate;

import com.xunmeng.common.annotation.Log;
import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.common.enums.BusinessType;
import com.xunmeng.llmgate.pojo.VoucherCode;
import com.xunmeng.llmgate.service.IAccountService;
import com.xunmeng.llmgate.service.IVoucherCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voucher")
@Api(tags = "兑换码管理")
public class VoucherCodeController extends BaseController {

    @Autowired
    private IVoucherCodeService voucherService;

    @Autowired
    private IAccountService accountService;

    /**
     * 添加兑换码（自动生成 code）
     */
    @ApiOperation("新增兑换码")
    @PreAuthorize("@ss.hasPermi('llmgate:voucher:add')")
    @Log(title = "兑换码管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody VoucherCode code) {
        String userId = getUserId();
        boolean success=voucherService.createVoucherCode(userId,code);
        return success ? AjaxResult.success(code.getVoucherCode()) : AjaxResult.error("生成失败");
    }

    /**
     * 禁用兑换码
     */
    @ApiOperation("禁用兑换码")
    @PreAuthorize("@ss.hasPermi('llmgate:voucher:disable')")
    @Log(title = "兑换码管理", businessType = BusinessType.UPDATE)
    @PostMapping("/disable/{code}")
    public AjaxResult disable(@PathVariable String code) {
        boolean success = voucherService.disableVoucherCode(code);
        return toAjax(success);
    }

    /**
     * 消费兑换码（仅逻辑调用）
     */
    @ApiOperation("消费兑换码")
    @PreAuthorize("@ss.hasPermi('voucher:exchange')")
    @PostMapping("/consume/{voucherCode}")
    public AjaxResult consume(@PathVariable String voucherCode) {
        String userId = getUserId();
        try {
            boolean success = accountService.redeemVoucherCode(userId, voucherCode);
            return toAjax(success);
        }catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 分页获取兑换码（可筛选：创建者、消费者、状态；时间倒序）
     */
    @ApiOperation("分页获取兑换码列表")
    @PreAuthorize("@ss.hasPermi('llmgate:voucher:list')")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(value = "creator", required = false) String creator,
            @RequestParam(value = "consumer", required = false) String consumer,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        startPage();
        List<VoucherCode> list = voucherService.getByConditions(creator, consumer, status);
        return getDataTable(list);
    }
}

