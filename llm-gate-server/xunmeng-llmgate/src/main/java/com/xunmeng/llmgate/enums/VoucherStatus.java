package com.xunmeng.llmgate.enums;

public enum VoucherStatus {
    DISABLED(0),
    UNUSED(-1),
    USED(1);

    private final int code;

    VoucherStatus(int code) { this.code = code; }
    public int getCode() { return code; }
}