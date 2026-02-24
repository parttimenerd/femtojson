package me.bechberger.util.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrettyPrinter {

    public static void prettyPrint(Object obj) {
        prettyPrint("", obj);
    }

    private static void prettyPrint(String indent, Object obj) {
        if (obj == null) {
            System.out.print("null");
        } else if (obj instanceof Boolean) {
            System.out.print(obj);
        } else if (obj instanceof Integer) {
            System.out.print(obj);
        } else if (obj instanceof Double) {
            System.out.print(obj);
        } else if (obj instanceof String) {
            System.out.print("\"" + escapeString((String) obj) + "\"");
        } else if (obj instanceof Map) {
            prettyPrintMap(indent, (Map<String, Object>) obj);
        } else if (obj instanceof ArrayList) {
            prettyPrintArray(indent, (List<Object>) obj);
        } else {
            System.out.print(obj);
        }
    }

    private static void prettyPrintMap(String indent, Map<String, Object> map) {
        if (map.isEmpty()) {
            System.out.print("{}");
            return;
        }

        System.out.println("{");
        String nextIndent = indent + "  ";
        boolean first = true;

        for (String key : map.keySet()) {
            if (!first) {
                System.out.println(",");
            }
            System.out.print(nextIndent + "\"" + escapeString(key) + "\": ");
            prettyPrint(nextIndent, map.get(key));
            first = false;
        }

        System.out.println();
        System.out.print(indent + "}");
    }

    private static void prettyPrintArray(String indent, List<Object> array) {
        if (array.isEmpty()) {
            System.out.print("[]");
            return;
        }

        System.out.println("[");
        String nextIndent = indent + "  ";
        boolean first = true;

        for (Object element : array) {
            if (!first) {
                System.out.println(",");
            }
            System.out.print(nextIndent);
            prettyPrint(nextIndent, element);
            first = false;
        }

        System.out.println();
        System.out.print(indent + "]");
    }

    private static String escapeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x0020 || c > 0x007E) {
                        // Escape non-ASCII and control characters as Unicode escape sequences
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}