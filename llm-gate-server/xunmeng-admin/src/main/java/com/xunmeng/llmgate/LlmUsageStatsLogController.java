package com.xunmeng.llmgate;


import com.xunmeng.common.core.controller.BaseController;
import com.xunmeng.common.core.page.TableDataInfo;
import com.xunmeng.llmgate.pojo.dto.LlmUsageStatsLogDTO;
import com.xunmeng.llmgate.service.ILlmUsageStatsLogService;
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
 * 模型调用日志 前端控制器
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/llmgate/llm-log")
@Api(tags = "模型调用日志管理")
public class LlmUsageStatsLogController extends BaseController {

    @Autowired
    private ILlmUsageStatsLogService logService;

    @ApiOperation("获取调用日志列表")
    @PreAuthorize("@ss.hasPermi('llmgate:llmlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String providerName,
                              @RequestParam(required = false) String modelName,
                              @RequestParam(required = false) String userName) {
        startPage();
        List<LlmUsageStatsLogDTO> list = logService.selectByConditions(providerName, modelName, userName);
        return getDataTable(list);
    }


    @ApiOperation("获取个人调用日志列表")
    @GetMapping("/selflist")
    public TableDataInfo selflist(@RequestParam(required = false) String providerName,
                              @RequestParam(required = false) String modelName) {
        startPage();
        String userId = getUserId();

        List<LlmUsageStatsLogDTO> list = logService.selectInSelfByConditions(providerName, modelName,userId);
        return getDataTable(list);
    }
}
