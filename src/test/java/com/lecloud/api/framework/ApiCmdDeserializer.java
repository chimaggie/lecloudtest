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
public class ApiCmdDeserializer extends JsonDeserializer<ApiCmd> {
    @Override
    public ApiCmd deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ApiCmd cmd = new ApiCmd();
        cmd.setUrl(node.get("url").textValue());
        cmd.setMethod(ApiCmd.Method.valueOf(node.get("method").textValue()));
        if (node.has("headers")) {
            JsonNode headersNode = node.get("headers");
            Map<String, String> headers = new HashMap<>();
            for (Iterator<String> iter = headersNode.fieldNames(); iter.hasNext(); ) {
                String key = iter.next();
                headers.put(key, headersNode.get(key).textValue());
            }
            cmd.setHeaders(headers);
        }
        if (node.has("body")) {
            JsonNode bodyNode = node.get("body");
            if (bodyNode.isValueNode()) cmd.setBody(bodyNode.asText());
            else cmd.setBody(bodyNode.toString());
        }
        return cmd;
    }
}
