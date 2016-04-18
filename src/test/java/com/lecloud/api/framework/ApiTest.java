package com.lecloud.api.framework;

/**
 * Created by hongyuechi on 4/16/16.
 */
public class ApiTest {
    private String name;
    private ApiCmd cmd;
    private ApiResult expect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiCmd getCmd() {
        return cmd;
    }

    public void setCmd(ApiCmd cmd) {
        this.cmd = cmd;
    }

    public ApiResult getExpect() {
        return expect;
    }

    public void setExpect(ApiResult expect) {
        this.expect = expect;
    }
}
