package com.xunmeng.llmgate.enums;

/**
 * API-KEY 启用状态枚举
 */
public enum ApiKeyStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final int code;
    private final String description;

    ApiKeyStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ApiKeyStatus fromCode(int code) {
        for (ApiKeyStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的 API-KEY 状态 code: " + code);
    }
}
