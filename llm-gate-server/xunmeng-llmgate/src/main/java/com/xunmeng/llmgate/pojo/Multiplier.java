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

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 倍率表
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("g_multiplier")
@ApiModel(value="Multiplier对象", description="倍率表")
public class Multiplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "模型名称")
    @TableField("model_name")
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    @ApiModelProperty(value = "输入花费/1k")
    @TableField("input_privice")
    @DecimalMin(value = "0.0", message = "输入单价不能为负")
    private Double inputPrivice;

    @ApiModelProperty(value = "输出花费/1k")
    @TableField("output_privice")
    @DecimalMin(value = "0.0", message = "输出单价不能为负")
    private Double outputPrivice;

    @ApiModelProperty(value = "倍率")
    @DecimalMin(value = "0.0", message = "倍率不能为负")
    private Double rate;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
