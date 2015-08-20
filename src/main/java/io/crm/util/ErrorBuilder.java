package io.crm.util;

import io.crm.core.model.Query;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by someone on 18/08/2015.
 */
final public class ErrorBuilder {
    private final Map<String, JsonArray> errorMap = new LinkedHashMap<>();

    public ErrorBuilder put(String field, JsonObject error) {
        JsonArray list = errorMap.get(field);
        if (list == null) {
            list = new JsonArray();
            errorMap.put(field, list);
        }
        list.add(error);
        return this;
    }

    public ErrorBuilder put(String field, String message) {
        return put(field, new JsonObject().put(Query.message, message));
    }

    public JsonObject get() {
        return new JsonObject(map(errorMap));
    }

    private Map<String, Object> map(Map map) {
        return map;
    }

    public static ErrorBuilder create() {
        return new ErrorBuilder();
    }
}
