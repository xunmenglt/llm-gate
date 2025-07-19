package com.xunmeng.llmgate.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "llmgate")
public class LlmGateProperties {

    private Server server = new Server();
    private int mianThreadNum;
    private int workThreadNum;
    private int timeOut;
    private int maxConnections;

    @Data
    public static class Server {
        private int port;
        private String prefix;
    }
}
