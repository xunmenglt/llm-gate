package com.xunmeng.llmgate;


import com.xunmeng.common.annotation.Log;
import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.common.enums.BusinessType;
import com.xunmeng.llmgate.enums.ApiKeyStatus;
import com.xunmeng.llmgate.enums.QuotaLimitType;
import com.xunmeng.llmgate.pojo.ApiKey;
import com.xunmeng.llmgate.pojo.dto.ApiKeyUsageSummaryDTO;
import com.xunmeng.llmgate.service.IApiKeyService;
import com.xunmeng.llmgate.utils.ApiKeyGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * api-key密钥表 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Api(tags = "API-KEY密钥管理")
@RestController
@RequestMapping("/apikey")
public class ApiKeyController extends BaseController {

    @Autowired
    private IApiKeyService apiKeyService;

    /**
     * API-KEY 列表查询（支持分页和条件过滤）
     */
    @ApiOperation("获取 API-KEY 列表")
    @PreAuthorize("@ss.hasPermi('llmgate:apikey:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "userId", required = false) String userId,
                              @RequestParam(value = "status", required = false) Integer status) {
        startPage();
        List<ApiKey> list = apiKeyService.selectByConditions(name, userId, status);
        return getDataTable(list);
    }


    @ApiOperation("获取 个人 API-KEY 列表")
    @GetMapping("/selflist")
    public TableDataInfo selflist(@RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "status", required = false) Integer status) {
        startPage();
        String userId = getUserId();
        List<ApiKey> list = apiKeyService.selectInSelfByConditions(name, userId, status);
        return getDataTable(list);
    }

    /**
     * 新增 API-KEY
     */
    @ApiOperation("创建 API-KEY")
    @PreAuthorize("@ss.hasPermi('llmgate:apikey:add')")
    @Log(title = "API-KEY 管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody ApiKey key) {
        LocalDateTime now = LocalDateTime.now();
        key.setCreateTime(now);
        key.setUpdateTime(now);
        key.setStatus(ApiKeyStatus.ENABLED.getCode());
        key.setUserName(getUserId());
        key.setKey(ApiKeyGenerator.generateApiKey());
        if (ObjectUtils.isNotEmpty(key.getExpiresTime()) && key.getExpiresTime().isBefore(now)){
            return AjaxResult.error("失效日期不能在当前日期之前");
        }
        return toAjax(apiKeyService.save(key));
    }


    /**
     * 更新 API-KEY 信息
     */
    @ApiOperation("修改 API-KEY 信息")
    @PreAuthorize("@ss.hasPermi('llmgate:apikey:edit')")
    @Log(title = "API-KEY 编辑", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public AjaxResult update(@Validated @RequestBody ApiKey key) {
        key.setUpdateTime(LocalDateTime.now());
        return toAjax(apiKeyService.updateById(key));
    }

    /**
     * 删除 API-KEY
     */
    @ApiOperation("删除 API-KEY")
    @PreAuthorize("@ss.hasPermi('llmgate:apikey:delete')")
    @Log(title = "API-KEY 删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ids}")
    public AjaxResult delete(@PathVariable Long[] ids) {
        return toAjax(apiKeyService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("/usage-summary/{apiKey}")
    @ApiOperation("获取 API-KEY 使用统计信息")
    @PreAuthorize("@ss.hasPermi('llmgate:apikey:summary')")
    public AjaxResult getApiKeyUsageSummary(@PathVariable String apiKey) {
        ApiKeyUsageSummaryDTO dto = apiKeyService.getUsageSummary(apiKey);
        return AjaxResult.success(dto);
    }
}