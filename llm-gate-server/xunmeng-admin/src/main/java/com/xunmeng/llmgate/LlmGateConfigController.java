package com.xunmeng.llmgate;

import com.xunmeng.common.annotation.Anonymous;
import com.xunmeng.llmgate.pojo.LlmGateProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@Api(tags = "系统配置接口")
public class LlmGateConfigController {
    @Autowired
    private LlmGateProperties llmgateProperties;

    @ApiOperation(value = "获取大模型服务网关配置", notes = "返回 application.yml 中配置的 llmgate 信息")
    @GetMapping("/llmgate")
    @Anonymous
    public LlmGateProperties getLlmgateConfig() {
        return llmgateProperties;
    }
}
