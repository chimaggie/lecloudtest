package com.lecloud.api.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hongyuechi on 4/16/16.
 */
public class ApiResultDeserializer extends JsonDeserializer<ApiResult> {
    @Override
    public ApiResult deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ApiResult result = new ApiResult();
        if (node.has("statusCode")) {
            result.setStatusCode(node.get("statusCode").intValue());
        }
        if (node.has("bodyValues")) {
            JsonNode bodyNode = node.get("bodyValues");
            Map<String, Object> values = new HashMap<>();
            for (Iterator<String> iter = bodyNode.fieldNames(); iter.hasNext();) {
                String key = iter.next();
                JsonNode val = bodyNode.get(key);
                if (val.isNumber()) values.put(key, val.numberValue());
                else values.put(key, val.asText());
            }
            result.setBodyValues(values);
        }
        return result;
    }
}
