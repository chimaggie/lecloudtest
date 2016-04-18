package com.lecloud.api.framework;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;

/**
 * Created by hongyuechi on 4/16/16.
 */
@JsonDeserialize(using = ApiTestSuiteDeserializer.class)
public class ApiTestSuite {
    private String baseUrl;
    private Map<String, Object> classConfig;
    @JsonIgnore
    private Map<String, Object> testConfig;
    private List<ApiTestSuiteConfig> beforeClassConfig;
    private List<ApiTestSuiteConfig> beforeTestConfig;
    private Map<String, ApiTest> tests;
    private List<ApiTestSuiteConfig> afterTestConfig;
    private List<ApiTestSuiteConfig> afterClassConfig;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, Object> getClassConfig() {
        return classConfig;
    }

    public void setClassConfig(Map<String, Object> classConfig) {
        this.classConfig = classConfig;
    }

    public List<ApiTestSuiteConfig> getBeforeClassConfig() {
        return beforeClassConfig;
    }

    public void setBeforeClassConfig(List<ApiTestSuiteConfig> beforeClassConfig) {
        this.beforeClassConfig = beforeClassConfig;
    }

    public Map<String, ApiTest> getTests() {
        return tests;
    }

    public void setTests(Map<String, ApiTest> tests) {
        this.tests = tests;
    }

    public List<ApiTestSuiteConfig> getAfterTestConfig() {
        return afterTestConfig;
    }

    public void setAfterTestConfig(List<ApiTestSuiteConfig> afterTestConfig) {
        this.afterTestConfig = afterTestConfig;
    }

    public List<ApiTestSuiteConfig> getAfterClassConfig() {
        return afterClassConfig;
    }

    public void setAfterClassConfig(List<ApiTestSuiteConfig> afterClassConfig) {
        this.afterClassConfig = afterClassConfig;
    }

    /////////////////////////////
    // framework functionality //
    /////////////////////////////
    public static ApiTestSuite build(Class testClass) throws Exception {
        ApiTestSuite suite = new ObjectMapper().readValue(testClass.getResourceAsStream(
                testClass.getSimpleName() + ".json"), ApiTestSuite.class);
        Field field = testClass.getDeclaredField("BASE_URL");
        field.setAccessible(true);
        String baseUrl = (String) field.get(null);
        if (baseUrl != null) suite.setBaseUrl(baseUrl);
        return suite;
    }

    public void beforeClassConfig(Class testClass) throws Exception {
        if (classConfig == null) classConfig = new HashMap<>();
        suiteConfig(classConfig, beforeClassConfig);
    }

    public void beforeTestConfig(Object testInstance) throws Exception {
//        suiteConfig(testInstance, beforeTestConfig);
    }

    public void afterTestConfig(Object testInstance) throws Exception {
//        suiteConfig(testInstance, afterTestConfig);
    }

    public void afterClassConfig(Class testClass) throws Exception {
//        suiteConfig(testClass, afterClassConfig);
    }

    private void suiteConfig(Map<String, Object> config, List<ApiTestSuiteConfig> input) throws Exception {
        for (ApiTestSuiteConfig ac : input) {
            Response resp = execCmd(ac.getApiCmd());
            if (ac.getName() == null) continue;
            config.put(ac.getName(), from(resp.body().asString()).get(ac.getResultPath()));
        }
    }

    public void asserting(ApiTest test) {
        Response resp = execCmd(test.getCmd());
        ApiResult expect = test.getExpect();
        if (expect.getStatusCode() != null) {
            assertThat(resp.getStatusCode(), is(expect.getStatusCode()));
        }
        if (expect.getBodyValues() == null) return;
        JsonPath jp = from(resp.getBody().asString());
        for (Map.Entry<String, Object> bodyExpect : expect.getBodyValues().entrySet()) {
            assertThat(jp.get(bodyExpect.getKey()), is(bodyExpect.getValue()));
        }
    }

    public Response execCmd(ApiCmd cmd) {
        RequestSpecification reqSpec = given();
        if (cmd.getHeaders() != null) {
            for (Map.Entry<String, String> header : cmd.getHeaders().entrySet()) {
                reqSpec = reqSpec.header(header.getKey(), header.getValue());
            }
        }
        if (cmd.getBody() != null) {
            reqSpec = reqSpec.body(cmd.getBody());
        }
        String path = cmd.getUrl();
        if (path.startsWith("/")) path = path.substring(1);
        String url = String.format("%s/%s", baseUrl, path);
        switch (cmd.getMethod()) {
            case POST:
                return reqSpec.post(url);
            case PUT:
                return reqSpec.put(url);
            case DELETE:
                return reqSpec.delete(url);
            case GET:
            default:
                return reqSpec.get(url);
        }
    }
}
