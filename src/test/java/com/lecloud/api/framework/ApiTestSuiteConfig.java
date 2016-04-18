package com.lecloud.api.framework;

/**
 * Created by hongyuechi on 4/16/16.
 */
public class ApiTestSuiteConfig {
    private String name;
    private ApiCmd apiCmd;
    private String resultPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiCmd getApiCmd() {
        return apiCmd;
    }

    public void setApiCmd(ApiCmd apiCmd) {
        this.apiCmd = apiCmd;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }
}
