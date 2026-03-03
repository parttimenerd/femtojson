package me.bechberger.util.json;

import java.util.List;
import java.util.Map;

/**
 * Prints JSON in compact format (single line, no extra whitespace)
 */
public class CompactPrinter {

    public static String compactPrint(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Integer) {
            return obj.toString();
        } else if (obj instanceof Double) {
            return obj.toString();
        } else if (obj instanceof String) {
            return "\"" + JSONStringUtil.escapeString((String) obj) + "\"";
        } else if (obj instanceof Map) {
            return compactPrintMap((Map<String, Object>) obj);
        } else if (obj instanceof List) {
            return compactPrintArray((List<Object>) obj);
        } else {
            return obj.toString();
        }
    }

    private static String compactPrintMap(Map<String, Object> map) {
        if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;

        for (String key : map.keySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(JSONStringUtil.escapeString(key)).append("\":");
            sb.append(compactPrint(map.get(key)));
            first = false;
        }

        sb.append("}");
        return sb.toString();
    }

    private static String compactPrintArray(List<Object> array) {
        if (array.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;

        for (Object element : array) {
            if (!first) {
                sb.append(",");
            }
            sb.append(compactPrint(element));
            first = false;
        }

        sb.append("]");
        return sb.toString();
    }
}