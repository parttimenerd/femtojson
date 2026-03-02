package me.bechberger.util.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrettyPrinter {

    public static String prettyPrint(Object obj) {
        return prettyPrint("", obj);
    }

    private static String prettyPrint(String indent, Object obj) {
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
        } else if (obj instanceof Map) {
            return prettyPrintMap(indent, (Map<String, Object>) obj);
        } else if (obj instanceof ArrayList) {
            return prettyPrintArray(indent, (List<Object>) obj);
        } else {
            return obj.toString();
        }
    }

    private static String prettyPrintMap(String indent, Map<String, Object> map) {
        if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        String nextIndent = indent + "  ";
        boolean first = true;

        for (String key : map.keySet()) {
            if (!first) {
                sb.append(",\n");
            }
            sb.append(nextIndent).append("\"").append(JSONStringUtil.escapeString(key)).append("\": ");
            sb.append(prettyPrint(nextIndent, map.get(key)));
            first = false;
        }

        sb.append("\n").append(indent).append("}");
        return sb.toString();
    }

    private static String prettyPrintArray(String indent, List<Object> array) {
        if (array.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        String nextIndent = indent + "  ";
        boolean first = true;

        for (Object element : array) {
            if (!first) {
                sb.append(",\n");
            }
            sb.append(nextIndent);
            sb.append(prettyPrint(nextIndent, element));
            first = false;
        }

        sb.append("\n").append(indent).append("]");
        return sb.toString();
    }

}