package com.lecloud.api.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by hongyuechi on 4/16/16.
 */
public class ApiTestSuiteDeserializer extends JsonDeserializer<ApiTestSuite> {
    @Override
    public ApiTestSuite deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ApiTestSuite suite = new ApiTestSuite();
        if (node.has("baseUrl")) suite.setBaseUrl(node.get("baseUrl").textValue());
        suite.setClassConfig(parseMap(node.get("classConfig"), String.class, String.class));
        suite.setBeforeClassConfig(parseList(node.get("beforeClassConfig"), ApiTestSuiteConfig.class));
        suite.setBeforeTestConfig(parseList(node.get("beforeTestConfig"), ApiTestSuiteConfig.class));
        suite.setAfterTestConfig(parseList(node.get("afterTestConfig"), ApiTestSuiteConfig.class));
        suite.setAfterClassConfig(parseList(node.get("afterClassConfig"), ApiTestSuiteConfig.class));
        List<ApiTest> testList = parseList(node.get("tests"), ApiTest.class);
        Map<String, ApiTest> testMap = new LinkedHashMap<>();
        for (ApiTest test : testList) {
            testMap.put(test.getName(), test);
        }
        suite.setTests(testMap);
        return suite;
    }

    private <T> List<T> parseList(JsonNode node, Class<T> listTypeClass) throws IOException {
        if (node == null) return null;
        return new ObjectMapper().readValue(node.toString(),
                TypeFactory.defaultInstance().constructCollectionType(List.class, listTypeClass));
    }

    private <K,V> Map<K,V> parseMap(JsonNode node, Class<K> mapKeyClass, Class<V> mapValClass) throws IOException {
        if (node == null) return null;
        return new ObjectMapper().readValue(node.toString(),
                TypeFactory.defaultInstance().constructMapType(Map.class, mapKeyClass, mapValClass));
    }
}
