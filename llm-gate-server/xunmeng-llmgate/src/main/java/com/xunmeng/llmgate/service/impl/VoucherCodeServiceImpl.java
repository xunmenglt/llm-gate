package com.xunmeng.llmgate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunmeng.llmgate.enums.VoucherStatus;
import com.xunmeng.llmgate.mapper.VoucherCodeMapper;
import com.xunmeng.llmgate.pojo.VoucherCode;
import com.xunmeng.llmgate.service.IVoucherCodeService;
import com.xunmeng.llmgate.utils.IdAndCodeGenerator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 兑换码 服务实现类
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
@Service
public class VoucherCodeServiceImpl extends ServiceImpl<VoucherCodeMapper, VoucherCode> implements IVoucherCodeService {

    @Autowired
    private VoucherCodeMapper voucherCodeMapper;


    private VoucherCode getUnConsumeVocherCode(String voucherCode){
        VoucherCode vocherCodeInfo = getVocherCodeInfo(voucherCode);
        if (vocherCodeInfo.getStatus()==VoucherStatus.UNUSED.getCode()){
            return vocherCodeInfo;
        }
        return null;
    }

    @Override
    public VoucherCode getVocherCodeInfo(String voucherCode) {
        return this.getOne(new QueryWrapper<VoucherCode>()
                .eq("voucher_code", voucherCode)
                .last("LIMIT 1"));
    }

    @Override
    public boolean exists(String voucherCode) {
        return this.count(new QueryWrapper<VoucherCode>()
                .eq("voucher_code", voucherCode)) > 0;
    }

    @Override
    public VoucherCode consume(String userId, String vocherCode) {
        VoucherCode code = getUnConsumeVocherCode(vocherCode);
        if (ObjectUtils.isEmpty(code)){
            return null;
        }
        code.setStatus(VoucherStatus.USED.getCode());
        code.setConsumer(userId);
        code.setUpdateTime(LocalDateTime.now());
        this.updateById(code);
        return code;
    }

    @Override
    public boolean disableVoucherCode(String voucherCode) {
        return this.update(
                new UpdateWrapper<VoucherCode>()
                        .eq("voucher_code", voucherCode)
                        .ne("status", VoucherStatus.USED.getCode()) // 已使用的不允许禁用，可选
                        .set("status", VoucherStatus.DISABLED.getCode())
                        .set("update_time", LocalDateTime.now())
        );
    }

    @Override
    public List<VoucherCode> getByConditions(String creator, String consumer, Integer status) {
        List<VoucherCode> list = voucherCodeMapper.selectByConditions(creator, consumer, status);
        return list;
    }

    @Override
    public boolean createVoucherCode(String userId,VoucherCode code) {
        code.setVoucherCode(IdAndCodeGenerator.generateVoucherCode()); // 可自定义逻辑
        code.setCreateTime(LocalDateTime.now());
        code.setUpdateTime(LocalDateTime.now());
        code.setCreator(userId);
        code.setStatus(VoucherStatus.UNUSED.getCode()); // 默认未消费
        boolean success = this.save(code);
        return success;
    }


}
