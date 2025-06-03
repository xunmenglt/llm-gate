package com.xunmeng.llmgate.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 模型调用日志
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("g_llm_usage_stats_log")
@ApiModel(value="LlmUsageStatsLog对象", description="模型调用日志")
public class LlmUsageStatsLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "日志ID")
    @TableField("log_id")
    private String logId;

    @ApiModelProperty(value = "提供商ID")
    @TableField("provider_id")
    private String providerId;

    @ApiModelProperty(value = "模型名称")
    @TableField("model_name")
    private String modelName;

    @ApiModelProperty(value = "API-KEY密钥")
    @TableField("api_key")
    private String apiKey;

    @ApiModelProperty(value = "提示词消耗")
    @TableField("input_tokens")
    private Long inputTokens;

    @ApiModelProperty(value = "补全消耗")
    @TableField("ouput_tokens")
    private Long ouputTokens;

    @ApiModelProperty(value = "额度")
    private Double quota;

    @ApiModelProperty(value = "是否错误调用")
    private Integer error;

    @ApiModelProperty(value = "错误类型")
    @TableField("error_type")
    private String errorType;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
