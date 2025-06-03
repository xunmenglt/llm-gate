package com.xunmeng.llmgate;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xunmeng.common.annotation.Log;
import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.common.core.pojo.AjaxResult;
import com.xunmeng.common.enums.BusinessType;
import com.xunmeng.common.utils.StringUtils;
import com.xunmeng.llmgate.pojo.Multiplier;
import com.xunmeng.llmgate.service.IMultiplierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 倍率表 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/multiplier")
@Api(tags = "倍率管理")
public class MultiplierController extends BaseController {



    @Autowired
    private IMultiplierService multiplierService;

    @ApiOperation(value = "获取倍率列表（可分页/按模型名过滤）")
    @PreAuthorize("@ss.hasPermi('llmgate:multiplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "modelName", required = false) String modelName) {
        // 启用分页
        startPage();

        List<Multiplier> list = multiplierService.selectByModelName(modelName);

        // 返回分页数据
        return getDataTable(list);
    }

    @ApiOperation(value = "新增倍率")
    @PreAuthorize("@ss.hasPermi('llmgate:multiplier:add')")
    @Log(title = "倍率管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody Multiplier multiplier) {
        Multiplier existing = multiplierService.getByModelName(multiplier.getModelName());
        if (existing != null && !existing.getModelName().equals("default")) {
            return AjaxResult.error("新增失败，模型 '" + multiplier.getModelName() + "' 的倍率已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        multiplier.setCreateTime(now);
        multiplier.setUpdateTime(now);

        boolean success = multiplierService.save(multiplier);
        return toAjax(success);
    }

    @ApiOperation(value = "删除倍率（支持批量）")
    @PreAuthorize("@ss.hasPermi('llmgate:multiplier:delete')")
    @Log(title = "倍率管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ids}")
    public AjaxResult delete(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("未提供要删除的 ID");
        }

        boolean success = multiplierService.removeByIds(Arrays.asList(ids));
        return toAjax(success);
    }

}

