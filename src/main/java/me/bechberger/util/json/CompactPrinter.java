package me.bechberger.util.json;

import java.util.List;
import java.util.Map;

/**
<<<<<<< HEAD
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
=======
 * Prints JSON in compact format (single line, no extra whitespace)
 */
public class CompactPrinter {

>>>>>>> 0bfff6c (Add compact printer)
    public static String compactPrint(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Integer) {
            return obj.toString();
        } else if (obj instanceof Double) {
            return obj.toString();
<<<<<<< HEAD
        } else if (obj instanceof String s) {
            return "\"" + JSONStringUtil.escapeString(s) + "\"";
        } else if (obj instanceof Map<?, ?> map) {
            return compactPrintMap(Util.asMap(map));
        } else if (obj instanceof List<?> list) {
            return compactPrintArray(Util.asList(list));
=======
        } else if (obj instanceof String) {
            return "\"" + JSONStringUtil.escapeString((String) obj) + "\"";
        } else if (obj instanceof Map) {
            return compactPrintMap((Map<String, Object>) obj);
        } else if (obj instanceof List) {
            return compactPrintArray((List<Object>) obj);
>>>>>>> 0bfff6c (Add compact printer)
        } else {
            return obj.toString();
        }
    }

<<<<<<< HEAD
    @SuppressWarnings("unchecked")
=======
>>>>>>> 0bfff6c (Add compact printer)
    private static String compactPrintMap(Map<String, Object> map) {
        if (map.isEmpty()) {
            return "{}";
        }
<<<<<<< HEAD
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
=======

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
>>>>>>> 0bfff6c (Add compact printer)
        return sb.toString();
    }

    private static String compactPrintArray(List<Object> array) {
        if (array.isEmpty()) {
            return "[]";
        }
<<<<<<< HEAD
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Object element : array) {
            if (!first) {
                sb.append(',');
=======

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;

        for (Object element : array) {
            if (!first) {
                sb.append(",");
>>>>>>> 0bfff6c (Add compact printer)
            }
            sb.append(compactPrint(element));
            first = false;
        }
<<<<<<< HEAD
        sb.append(']');
        return sb.toString();
    }
}
=======

        sb.append("]");
        return sb.toString();
    }
}
>>>>>>> 0bfff6c (Add compact printer)
