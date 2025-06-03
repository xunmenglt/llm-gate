package com.xunmeng.llmgate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunmeng.llmgate.pojo.VoucherCode;

import java.util.List;

/**
 * <p>
 * 兑换码 服务类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface IVoucherCodeService extends IService<VoucherCode> {
    VoucherCode getVocherCodeInfo(String vocherCode);

    boolean exists(String voucherCode);

    VoucherCode consume(String userId,String vocherCode);

    /**
     * 禁用指定兑换码
     *
     * @param voucherCode 兑换码字符串
     * @return 是否禁用成功
     */
    boolean disableVoucherCode(String voucherCode);

    List<VoucherCode> getByConditions(String creator, String consumer, Integer status);

    boolean createVoucherCode(String userId,VoucherCode code);
}
