package com.xunmeng.llmgate.proxy.context;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LLMGateContext {

    private int port;

    private String prefix;

    private int mianThreadNum;

    private int workThreadNum;

    private long timeOut;

    private int maxConnections;

}
