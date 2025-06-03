package com.xunmeng.llmgate;


import com.xunmeng.common.annotation.Log;
import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.common.enums.BusinessType;
import com.xunmeng.llmgate.pojo.ProvuiderModelMapping;
import com.xunmeng.llmgate.pojo.dto.ProviderModelMappingDTO;
import com.xunmeng.llmgate.service.IProvuiderModelMappingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 大模型提供商-模型映射表 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/llmgate/model-mapping")
@Api(tags = "模型映射管理")
public class ProvuiderModelMappingController extends BaseController {

    @Autowired
    private IProvuiderModelMappingService mappingService;

    @ApiOperation("新增模型映射")
    @PreAuthorize("@ss.hasPermi('llmgate:modelmapping:add')")
    @Log(title = "模型映射", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ProvuiderModelMapping mapping) {
        if (ObjectUtils.isEmpty(mapping.getProviderId())){
            return AjaxResult.error("服务商不能为空");
        }
        if (ObjectUtils.isNotEmpty(mapping.getModelNameAlias())){
            String aliasStr = mapping.getModelNameAlias();
            try {
                String[] split = aliasStr.split(",");
            }catch (Exception e){
                return AjaxResult.error("模型别名列表输入异常");
            }
        }
        mapping.setCreateTime(LocalDateTime.now());
        mapping.setUpdateTime(LocalDateTime.now());
        return toAjax(mappingService.save(mapping));
    }

    @ApiOperation("删除模型映射")
    @PreAuthorize("@ss.hasPermi('system:modelmapping:delete')")
    @Log(title = "模型映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ids}")
    public AjaxResult delete(@PathVariable Long[] ids) {
        return toAjax(mappingService.removeByIds(Arrays.asList(ids)));
    }

    @ApiOperation("获取模型映射列表（可分页+过滤）")
    @PreAuthorize("@ss.hasPermi('system:modelmapping:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String providerName,
                              @RequestParam(required = false) String modelName) {
        startPage();
        List<ProviderModelMappingDTO> list = mappingService.selectByConditions(providerName, modelName);
        return getDataTable(list);
    }
}
