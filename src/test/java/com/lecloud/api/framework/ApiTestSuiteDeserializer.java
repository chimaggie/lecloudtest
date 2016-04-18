package com.lecloud.api.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hongyuechi on 4/16/16.
 */
public class ApiTestSuiteDeserializer extends JsonDeserializer<ApiTestSuite> {
    @Override
    public ApiTestSuite deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ApiTestSuite suite = new ApiTestSuite();
        if (node.has("baseUrl")) suite.setBaseUrl(node.get("baseUrl").textValue());
        if (node.has("classConfig")) {
            JsonNode classConfigNode = node.get("classConfig");
            Map<String, Object> configs = new HashMap<>();
            for (Iterator<String> iter = classConfigNode.fieldNames(); iter.hasNext();) {
                String key = iter.next();
                JsonNode val = classConfigNode.get(key);
                if (val.isNumber()) configs.put(key, val.numberValue());
                else configs.put(key, val.asText());
            }
            suite.setClassConfig(configs);
        }
        suite.setBeforeClassConfig(this.<ApiTestSuiteConfig>getConfigs(node, "beforeClassConfig"));
        suite.setAfterTestConfig(this.<ApiTestSuiteConfig>getConfigs(node, "afterTestConfig"));
        suite.setAfterClassConfig(this.<ApiTestSuiteConfig>getConfigs(node, "afterClassConfig"));
        List<ApiTest> testList = getConfigs(node, "tests");
        Map<String, ApiTest> testMap = new HashMap<>();
        for (ApiTest test : testList) {
            testMap.put(test.getName(), test);
        }
        suite.setTests(testMap);
        return suite;
    }

    private <T> List<T> getConfigs(JsonNode node, String key) throws IOException {
        if (!node.has(key)) {
            return null;
        }
        return new ObjectMapper().readValue(node.get(key).toString(), new TypeReference<List<T>>(){});
    }
}
