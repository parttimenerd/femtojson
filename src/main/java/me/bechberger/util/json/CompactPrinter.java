package me.bechberger.util.json;

import java.util.List;
import java.util.Map;

/**
 * Compact (single-line, no extra whitespace) JSON printer.
 * <p>
 * Delegates string escaping to {@link JSONStringUtil} so that emoji and other
 * supplementary characters are properly encoded as surrogate-pair escapes.
 */
public class CompactPrinter {

    public CompactPrinter() {
    }

    /**
     * Serialize an object graph to a compact JSON string.
     *
     * @param obj a JSON-like value (null, Boolean, Integer, Double, String, Map, List)
     * @return the compact JSON representation
     */
    public static String compactPrint(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Integer) {
            return obj.toString();
        } else if (obj instanceof Double) {
            return obj.toString();
        } else if (obj instanceof String s) {
            return "\"" + JSONStringUtil.escapeString(s) + "\"";
        } else if (obj instanceof Map<?, ?> map) {
            return compactPrintMap(Util.asMap(map));
        } else if (obj instanceof List<?> list) {
            return compactPrintArray(Util.asList(list));
        } else {
            return obj.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private static String compactPrintMap(Map<String, Object> map) {
        if (map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (String key : map.keySet()) {
            if (!first) {
                sb.append(',');
            }
            sb.append('"').append(JSONStringUtil.escapeString(key)).append("\":");
            sb.append(compactPrint(map.get(key)));
            first = false;
        }
        sb.append('}');
        return sb.toString();
    }

    private static String compactPrintArray(List<Object> array) {
        if (array.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Object element : array) {
            if (!first) {
                sb.append(',');
            }
            sb.append(compactPrint(element));
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }
}
