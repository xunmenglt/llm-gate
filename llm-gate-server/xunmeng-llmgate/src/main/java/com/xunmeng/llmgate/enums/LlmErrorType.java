package com.xunmeng.llmgate.enums;

/**
 * 大模型调用错误类型枚举
 */
public enum LlmErrorType {

    UNKNOWN("UNKNOWN", "未知错误"),
    MAX_CONCURRENCY("MAX_CONCURRENCY", "超出最大并发限制"),
    TIMEOUT("TIMEOUT", "调用超时"),
    INVALID_KEY("INVALID_KEY", "API-KEY 无效"),
    MODEL_NOT_FOUND("MODEL_NOT_FOUND", "模型不存在"),
    QUOTA_EXCEEDED("QUOTA_EXCEEDED", "额度不足");

    private final String code;
    private final String description;

    LlmErrorType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LlmErrorType fromCode(String code) {
        for (LlmErrorType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
