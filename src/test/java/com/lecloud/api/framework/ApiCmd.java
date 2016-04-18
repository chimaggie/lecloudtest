package com.lecloud.api.framework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

/**
 * Created by hongyuechi on 4/16/16.
 */
@JsonDeserialize(using = ApiCmdDeserializer.class)
public class ApiCmd {
    private String url;
    private Method method;
    private Map<String, String> headers;
    private String body;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public enum Method {
        GET, POST, PUT, DELETE;
    }
}
