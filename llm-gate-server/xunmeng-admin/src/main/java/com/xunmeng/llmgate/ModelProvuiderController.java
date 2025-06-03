package com.xunmeng.llmgate;


import com.xunmeng.common.annotation.Log;
import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.common.enums.BusinessType;
import com.xunmeng.llmgate.pojo.ModelProvuider;
import com.xunmeng.llmgate.pojo.dto.ProviderWithModelsDTO;
import com.xunmeng.llmgate.service.IModelProvuiderService;
import com.xunmeng.llmgate.utils.IdAndCodeGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 大模型提供商 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Api(tags = "大模型提供商管理接口")
@RestController
@RequestMapping("/system/provider")
public class ModelProvuiderController extends BaseController {

    @Autowired
    private IModelProvuiderService providerService;

    @ApiOperation("获取大模型提供商列表")
    @PreAuthorize("@ss.hasPermi('llmgate:provider:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "providerName", required = false) String providerName,
                              @RequestParam(value = "type", required = false) String type) {
        startPage();
        List<ModelProvuider> list = providerService.selectByConditions(providerName, type);
        return getDataTable(list);
    }

    @ApiOperation("新增提供商")
    @PreAuthorize("@ss.hasPermi('llmgate:provider:add')")
    @Log(title = "大模型提供商", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ModelProvuider provider) {
        provider.setProviderId(IdAndCodeGenerator.generateProviderId());
        provider.setCreateTime(LocalDateTime.now());
        provider.setUpdateTime(LocalDateTime.now());
        return toAjax(providerService.save(provider));
    }

    @ApiOperation("修改提供商")
    @PreAuthorize("@ss.hasPermi('llmgate:provider:edit')")
    @Log(title = "大模型提供商", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public AjaxResult update(@RequestBody ModelProvuider provider) {
        provider.setUpdateTime(LocalDateTime.now());
        return toAjax(providerService.updateById(provider));
    }

    @ApiOperation("删除提供商")
    @PreAuthorize("@ss.hasPermi('llmgate:provider:delete')")
    @Log(title = "大模型提供商", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        return toAjax(providerService.removeById(id));
    }

    @ApiOperation("获取提供商详情（含模型列表）")
    @GetMapping("/detail/{providerId}")
    public AjaxResult detail(@PathVariable String providerId) {
        ProviderWithModelsDTO dto = providerService.getProviderWithModels(providerId);
        return AjaxResult.success(dto);
    }
}