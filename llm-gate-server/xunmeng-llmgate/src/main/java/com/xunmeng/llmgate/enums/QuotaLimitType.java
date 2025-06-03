package com.xunmeng.llmgate.enums;

/**
 * API-KEY额度类型枚举：是否为无限额度
 */
public enum QuotaLimitType {
    UNLIMITED(0, "无限额度"),
    LIMITED(1, "有限额度");

    private final int code;
    private final String description;

    QuotaLimitType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static QuotaLimitType fromCode(int code) {
        for (QuotaLimitType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知额度类型 code: " + code);
    }
}
