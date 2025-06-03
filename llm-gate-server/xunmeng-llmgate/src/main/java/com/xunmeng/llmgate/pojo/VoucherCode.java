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
 * 兑换码
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("g_voucher_code")
@ApiModel(value="VoucherCode对象", description="兑换码")
public class VoucherCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "兑换码")
    @TableField("voucher_code")
    private String voucherCode;

    @ApiModelProperty(value = "额度")
    @DecimalMin(value = "0.0", message = "额度单价不能为负")
    private Double quota;

    @ApiModelProperty(value = "创建用户")
    @NotBlank(message = "创建者不能为空")
    private String creator;

    @ApiModelProperty(value = "消费用户")
    private String consumer;

    @ApiModelProperty(value = "状态(‘-1’未被消费，1已被消费，0禁用)")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
