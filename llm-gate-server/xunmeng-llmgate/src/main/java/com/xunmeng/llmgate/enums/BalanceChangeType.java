package com.xunmeng.llmgate.enums;

public enum BalanceChangeType {
    MODEL_CALL("模型调用"),
    VOUCHER_CODE_USE("兑换码使用"),
    SYSTEM_ADJUST("系统调整"),
    ADMIN_RECHARGE("管理员充值");

    private final String description;

    BalanceChangeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return this.name(); // 获取枚举的英文名称作为代码
    }
}
