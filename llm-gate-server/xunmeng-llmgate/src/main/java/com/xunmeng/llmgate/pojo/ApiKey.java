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

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * <p>
 * api-key密钥表
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("g_api_key")
@ApiModel(value="ApiKey对象", description="api-key密钥表")
public class ApiKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "密钥名称")
    @TableField("`name`")
    private String name;

    @ApiModelProperty(value = "用户id")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "API-KEY密钥")
    @TableField("`key`")
    private String key;

    @ApiModelProperty(value = "状态(是否开启)")
    private Integer status;

    @ApiModelProperty(value = "过期时间")
    @TableField("expires_time")
    private LocalDateTime expiresTime;

    @ApiModelProperty(value = "额度")
    @DecimalMin(value = "0.0", message = "额度不能为负")
    private Double quota;

    @ApiModelProperty(value = "是否无限额度")
    private Integer unlimited;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
