package com.xunmeng.llmgate.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class ApiKeyGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String PREFIX = "sk-"; // Key前缀，可以改成你想要的
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成一个默认长度的API Key
     */
    public static String generateApiKey() {
        return generateApiKey(32);
    }

    /**
     * 生成指定长度的API Key
     *
     * @param length key的总长度（不包含前缀）
     */
    public static String generateApiKey(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return PREFIX + sb.toString();
    }

    /**
     * 生成带UUID混合的API Key（可选）
     */
    public static String generateUuidBasedKey() {
        return PREFIX + UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(generateApiKey());
        System.out.println(generateApiKey());
        System.out.println(generateApiKey());
        System.out.println(generateApiKey());

    }
}
