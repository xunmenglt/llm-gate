package com.xunmeng.llmgate.proxy.context;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class RequestContext {
    private String path;
    private HttpMethod method;
    private String clientIp;
    private String traceId;     // 可用于链路追踪
    private String userId;      // 可结合用户认证模块填充

    private HttpHeaders headers;

    private Map<String, List<String>> queryParams;

    private String body; // 请求体文本

    // 构造方法
    public RequestContext(String path, HttpMethod method, String clientIp,HttpHeaders headers, Map<String, List<String>> queryParams, String body) {
        this.path = path;
        this.method = method;
        this.clientIp = clientIp;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

}
