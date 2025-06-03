package com.xunmeng.llmgate.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class IdAndCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    // 生成兑换码（如：VOUCHER-202506-16位随机十六进制）
    public static String generateVoucherCode() {
        return "VOUCHER-" + getCurrentYearMonth() + "-" + randomHex(16);
    }

    // 生成日志ID（UUID无“-”）
    public static String generateLogId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 生成提供商ID（如：PROV-2025-XXXX）
    public static String generateProviderId() {
        return "PROV-" + getCurrentYear() + "-" + randomAlphaNumeric(6);
    }

    // 生成API-KEY（Base64编码的128位随机值）
    public static String generateApiKey() {
        byte[] keyBytes = new byte[24]; // 24字节 = 32字符Base64（安全）
        random.nextBytes(keyBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
    }

    // ======================== 辅助函数 ========================

    private static String getCurrentYearMonth() {
        java.time.LocalDate now = java.time.LocalDate.now();
        return now.getYear() + String.format("%02d", now.getMonthValue());
    }

    private static String getCurrentYear() {
        return String.valueOf(java.time.LocalDate.now().getYear());
    }

    private static String randomHex(int length) {
        byte[] bytes = new byte[length / 2];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String randomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
