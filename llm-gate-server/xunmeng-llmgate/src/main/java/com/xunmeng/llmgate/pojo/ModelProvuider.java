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
 * 大模型提供商
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("g_model_provuider")
@ApiModel(value="ModelProvuider对象", description="大模型提供商")
public class ModelProvuider implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "提供商ID")
    @TableField("provider_id")
    private String providerId;

    @ApiModelProperty(value = "提供商名称")
    @TableField("provider_name")
    private String providerName;

    @ApiModelProperty(value = "接口服务类型")
    private String type;

    @ApiModelProperty(value = "密钥")
    @TableField("api_key")
    private String apiKey;

    @ApiModelProperty(value = "代理路径")
    @TableField("proxy_url")
    private String proxyUrl;

    @ApiModelProperty(value = "最大并发数")
    @TableField("max_concurrency")
    private Integer maxConcurrency;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


    @TableField(exist = false)
    private ProvuiderModelMapping modelMapping;




}
