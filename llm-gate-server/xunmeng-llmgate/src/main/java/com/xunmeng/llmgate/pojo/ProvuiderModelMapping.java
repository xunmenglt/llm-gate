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
 * 大模型提供商-模型映射表
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("g_provuider_model_mapping")
@ApiModel(value="ProvuiderModelMapping对象", description="大模型提供商-模型映射表")
public class ProvuiderModelMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "提供商ID")
    @TableField("provider_id")
    private String providerId;

    @ApiModelProperty(value = "模型名称")
    @TableField("model_name")
    private String modelName;

    @ApiModelProperty(value = "模型别名列表")
    @TableField("model_name_alias")
    private String modelNameAlias;

    @ApiModelProperty(value = "最大并发数")
    @TableField("max_concurrency")
    private Integer maxConcurrency;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
