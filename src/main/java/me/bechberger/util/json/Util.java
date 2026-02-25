package me.bechberger.util.json;

import java.util.List;
import java.util.Map;

/**
 * Utility methods for working with JSON-like structures (maps and lists).
 */
public class Util {

    private Util() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        } else {
            throw new IllegalArgumentException("Expected a Map but got: " + obj.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object obj) {
        if (obj instanceof List<?>) {
            return (List<Object>) obj;
        } else {
            throw new IllegalArgumentException("Expected a List but got: " + obj.getClass());
        }
    }
}