package com.xunmeng.llmgate.enums;

public enum ModelProviderType {
    OPENAI("openai"),
    CLAUDE("claude");

    private final String code;

    ModelProviderType(String code) {
        this.code = code;
    }


    public String getCode() {
        return this.code; // 获取枚举的英文名称作为代码
    }


    // 根据code获取枚举值
    public static ModelProviderType fromCode(String code) {
        for (ModelProviderType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ModelProviderType code: " + code);
    }

    // 可选：提供安全转换方法，不抛出异常
    public static ModelProviderType fromCodeSafe(String code) {
        for (ModelProviderType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return OPENAI;
    }
}
