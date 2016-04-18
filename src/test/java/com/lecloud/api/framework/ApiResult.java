package com.lecloud.api.framework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

/**
 * Created by hongyuechi on 4/16/16.
 */
@JsonDeserialize(using = ApiResultDeserializer.class)
public class ApiResult {
    private Integer statusCode;
    private Map<String, Object> bodyValues;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, Object> getBodyValues() {
        return bodyValues;
    }

    public void setBodyValues(Map<String, Object> bodyValues) {
        this.bodyValues = bodyValues;
    }
}
