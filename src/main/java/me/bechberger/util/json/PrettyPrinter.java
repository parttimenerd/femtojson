package me.bechberger.util.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrettyPrinter {

    public static String prettyPrint(Object obj) {
        return prettyPrint("", obj, false);
    }

    public static String compactPrint(Object obj) {
        return prettyPrint("", obj, true);
    }

    private static String prettyPrint(String indent, Object obj, boolean compact) {
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
            return prettyPrintMap(indent, (Map<String, Object>) obj, compact);
        } else if (obj instanceof ArrayList) {
            return prettyPrintArray(indent, (List<Object>) obj, compact);
        } else {
            return obj.toString();
        }
    }

    private static String prettyPrintMap(String indent, Map<String, Object> map, boolean compact) {
        if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (!compact) {
            sb.append("\n");
        }
        String nextIndent = indent + "  ";
        boolean first = true;

        for (String key : map.keySet()) {
            if (!first) {
                sb.append(",");
                if (!compact) {
                    sb.append("\n");
                }
            }
            if (!compact) {
                sb.append(nextIndent);
            }
            sb.append("\"").append(JSONStringUtil.escapeString(key)).append("\":");
            if (!compact) {
                sb.append(" ");
            }
            sb.append(prettyPrint(nextIndent, map.get(key), compact));
            first = false;
        }
        if (!compact) {
            sb.append("\n").append(indent);
        }
        sb.append("}");
        return sb.toString();
    }

    private static String prettyPrintArray(String indent, List<Object> array, boolean compact) {
        if (array.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (!compact) {
            sb.append("\n");
        }
        String nextIndent = indent + "  ";
        boolean first = true;

        for (Object element : array) {
            if (!first) {
                sb.append(",");
                if (!compact) {
                    sb.append("\n");
                }
            }
            if (!compact) {
                sb.append(nextIndent);
            }
            sb.append(prettyPrint(nextIndent, element, compact));
            first = false;
        }

        if (!compact) {
            sb.append("\n").append(indent);
        }
        sb.append("]");
        return sb.toString();
    }
}