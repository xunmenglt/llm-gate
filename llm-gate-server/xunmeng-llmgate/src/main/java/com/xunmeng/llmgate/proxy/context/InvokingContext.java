package com.xunmeng.llmgate.proxy.context;


import com.xunmeng.llmgate.pojo.Account;
import com.xunmeng.llmgate.pojo.ApiKey;
import com.xunmeng.llmgate.pojo.ModelProvuider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvokingContext {
    private String userId;
    private ApiKey apiKey;
    private Account account;

    private String modelName;

    private String uri;

    private ModelProvuider modelProvuider;

    private int proxyPort;

    private String proxyHost;
}
