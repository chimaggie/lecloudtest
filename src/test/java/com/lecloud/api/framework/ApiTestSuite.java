package com.lecloud.api.framework;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junitparams.JUnitParamsRunner.$;
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
    private Map<String, String> classConfig;
    @JsonIgnore
    private Map<String, String> testConfig;
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

    public Map<String, String> getClassConfig() {
        return classConfig;
    }

    public void setClassConfig(Map<String, String> classConfig) {
        this.classConfig = classConfig;
    }

    public List<ApiTestSuiteConfig> getBeforeClassConfig() {
        return beforeClassConfig;
    }

    public void setBeforeClassConfig(List<ApiTestSuiteConfig> beforeClassConfig) {
        this.beforeClassConfig = beforeClassConfig;
    }

    public List<ApiTestSuiteConfig> getBeforeTestConfig() {
        return beforeTestConfig;
    }

    public void setBeforeTestConfig(List<ApiTestSuiteConfig> beforeTestConfig) {
        this.beforeTestConfig = beforeTestConfig;
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
    public static ApiTestSuite build(Class testClass) {
        ApiTestSuite suite;
        try {
            suite = new ObjectMapper().readValue(testClass.getResourceAsStream(
                    testClass.getSimpleName() + ".json"), ApiTestSuite.class);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            Field field = testClass.getDeclaredField("BASE_URL");
            field.setAccessible(true);
            String baseUrl = (String) field.get(null);
            if (baseUrl != null) suite.setBaseUrl(baseUrl);
        } catch (Exception e) {}
        ApiTestSuiteParameterProvider.parameters = suite.getTests().keySet().toArray(new Object[]{});
        return suite;
    }

    public void beforeClassConfig() throws Exception {
        if (classConfig == null) classConfig = new HashMap<>();
        suiteConfig(classConfig, beforeClassConfig);
    }

    public void beforeTestConfig() throws Exception {
        if (testConfig == null) testConfig = new HashMap<>();
        testConfig.clear();
        suiteConfig(testConfig, beforeTestConfig);
    }

    public void afterTestConfig() throws Exception {
        if (testConfig == null) testConfig = new HashMap<>();
        suiteConfig(testConfig, afterTestConfig);
    }

    public void afterClassConfig() throws Exception {
        if (classConfig == null) classConfig = new HashMap<>();
        suiteConfig(classConfig, afterClassConfig);
    }

    private void suiteConfig(Map<String, String> config, List<ApiTestSuiteConfig> input) throws Exception {
        if (input == null) return;
        for (ApiTestSuiteConfig ac : input) {
            Response resp = execCmd(ac.getApiCmd());
            if (ac.getName() == null) continue;
            config.put(ac.getName(), from(resp.body().asString()).getString(ac.getResultPath()));
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

    private Response execCmd(ApiCmd cmd) {
        RequestSpecification reqSpec = given();
        if (cmd.getHeaders() != null) {
            for (Map.Entry<String, String> header : cmd.getHeaders().entrySet()) {
                reqSpec = reqSpec.header(header.getKey(), handleReference(header.getValue()));
            }
        }
        if (cmd.getBody() != null) {
            reqSpec = reqSpec.body(handleReference(cmd.getBody()));
        }
        String path = handleReference(cmd.getUrl());
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

    private String handleReference(String valStr) {
        if (valStr == null) return valStr;
        String pattern = "\\$\\{(\\w+)\\}";
        Matcher m = Pattern.compile(pattern).matcher(valStr);
        while (m.find()) {
            String ref = m.group(1);
            String refVal = null;
            if (testConfig != null && testConfig.containsKey(ref)) refVal = testConfig.get(ref);
            else if (classConfig != null) refVal = classConfig.get(ref);
            if (refVal != null) {
                valStr = valStr.replaceAll(pattern, refVal);
            }
        }
        return valStr;
    }
}
