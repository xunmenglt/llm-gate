package com.xunmeng.llmgate.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAIStreamResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
    private List<Choice> choices;
    private Usage usage;

    @Data
    public static class Choice {
        private int index;
        private Delta delta;
        private Object logprobs;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    public static class Delta {
        private String content;
        private String reasoning_content; // 新增字段
        private String role; // 新增字段
    }

    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
        @JsonProperty("prompt_tokens_details")
        private PromptTokensDetails promptTokensDetails;
        @JsonProperty("prompt_cache_hit_tokens")
        private int promptCacheHitTokens;
        @JsonProperty("prompt_cache_miss_tokens")
        private int promptCacheMissTokens;
    }

    @Data
    public static class PromptTokensDetails {
        @JsonProperty("cached_tokens")
        private int cachedTokens;
    }
}