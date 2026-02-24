package me.bechberger.util.json;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PrettyPrinterTest {

    /**
     * Test data: pairs of (object to pretty print, expected formatted output)
     */
    static Stream<org.junit.jupiter.params.provider.Arguments> prettyPrintCases() {
        return Stream.of(
                // Null value
                Arguments.of(
                        null,
                        "null"
                ),

                // Booleans
                Arguments.of(
                        true,
                        "true"
                ),
                Arguments.of(
                        false,
                        "false"
                ),

                // Numbers
                Arguments.of(
                        0.0,
                        "0.0"
                ),
                Arguments.of(
                        42.0,
                        "42.0"
                ),
                Arguments.of(
                        -1.0,
                        "-1.0"
                ),
                Arguments.of(
                        3.14,
                        "3.14"
                ),

                // Strings
                Arguments.of(
                        "hello",
                        "\"hello\""
                ),
                Arguments.of(
                        "",
                        "\"\""
                ),
                Arguments.of(
                        "hello world",
                        "\"hello world\""
                ),
                Arguments.of(
                        "line1\nline2",
                        "\"line1\\nline2\""
                ),
                Arguments.of(
                        "tab\there",
                        "\"tab\\there\""
                ),

                // Empty array
                Arguments.of(
                        new ArrayList<>(),
                        "[]"
                ),

                // Empty object
                Arguments.of(
                        new HashMap<>(),
                        "{}"
                ),

                // Single-element array
                Arguments.of(
                        new ArrayList<>(Collections.singletonList(1.0)),
                        "[\n  1.0\n]"
                ),

                // Single-element object
                Arguments.of(
                        createMap("key", "value"),
                        "{\n  \"key\": \"value\"\n}"
                ),

                // Multi-element array
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)),
                        "[\n  1.0,\n  2.0,\n  3.0\n]"
                ),

                // Multi-element object
                Arguments.of(
                        createMap("a", 1.0, "b", 2.0),
                        "{\n  \"a\": 1.0,\n  \"b\": 2.0\n}"
                ),

                // Array with strings
                Arguments.of(
                        new ArrayList<>(Arrays.asList("foo", "bar", "baz")),
                        "[\n  \"foo\",\n  \"bar\",\n  \"baz\"\n]"
                ),

                // Array with mixed types
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1.0, "two", true, null)),
                        "[\n  1.0,\n  \"two\",\n  true,\n  null\n]"
                ),

                // Nested array
                Arguments.of(
                        new ArrayList<>(Arrays.asList(
                                new ArrayList<>(Arrays.asList(1.0, 2.0)),
                                new ArrayList<>(Arrays.asList(3.0, 4.0))
                        )),
                        "[\n  [\n    1.0,\n    2.0\n  ],\n  [\n    3.0,\n    4.0\n  ]\n]"
                ),

                // Nested object
                Arguments.of(
                        createMap("outer", createMap("inner", "value")),
                        "{\n  \"outer\": {\n    \"inner\": \"value\"\n  }\n}"
                ),

                // Complex nested structure
                Arguments.of(
                        createMap("items", new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "nested", createMap("key", "value")),
                        "{\n  \"items\": [\n    1.0,\n    2.0,\n    3.0\n  ],\n  \"nested\": {\n    \"key\": \"value\"\n  }\n}"
                ),

                // Object with empty nested structures
                Arguments.of(
                        createMap("empty_obj", new HashMap<>(), "empty_arr", new ArrayList<>()),
                        "{\n  \"empty_obj\": {},\n  \"empty_arr\": []\n}"
                ),

                // String with escaped characters
                Arguments.of(
                        "\"quoted\"",
                        "\"\\\"quoted\\\"\""
                ),
                Arguments.of(
                        "back\\slash",
                        "\"back\\\\slash\""
                ),

                // Array containing empty collections
                Arguments.of(
                        new ArrayList<>(Arrays.asList(new ArrayList<>(), new HashMap<>())),
                        "[\n  [],\n  {}\n]"
                ),

                // Deeply nested structure (3 levels)
                Arguments.of(
                        createMap("a", createMap("b", createMap("c", 1.0))),
                        "{\n  \"a\": {\n    \"b\": {\n      \"c\": 1.0\n    }\n  }\n}"
                ),

                // Array with nested objects
                Arguments.of(
                        new ArrayList<>(Arrays.asList(
                                createMap("id", 1.0),
                                createMap("id", 2.0)
                        )),
                        """
                        [
                          {
                            "id": 1.0
                          },
                          {
                            "id": 2.0
                          }
                        ]"""
                )
        );
    }

    /**
     * Test that objects are pretty printed with correct formatting
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("prettyPrintCases")
    public void testPrettyPrint(Object obj, String expected) {
        String actual = prettyPrintToString(obj);
        assertEquals(expected, actual, "Pretty printed output should match expected format");
    }

    /**
     * Test that pretty printed output is valid JSON that can be parsed back
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("prettyPrintCases")
    public void testPrettyPrintIsValidJSON(Object obj, String expected) throws IOException {
        String prettyPrinted = prettyPrintToString(obj);

        // Should not throw an exception when parsing
        JSONParser parser = new JSONParser(prettyPrinted);
        Object parsed = parser.parseJson();

        assertEquals(obj, parsed, "Pretty printed JSON should parse back to the same object");
    }

    /**
     * Helper method to pretty print an object to a string
     */
    private static String prettyPrintToString(Object obj) {
        return prettyPrintToString("", obj);
    }

    private static String prettyPrintToString(String indent, Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Integer) {
            return obj.toString();
        } else if (obj instanceof Double) {
            return obj.toString();
        } else if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        } else if (obj instanceof Map) {
            return prettyPrintMapToString(indent, (Map<String, Object>) obj);
        } else if (obj instanceof List) {
            return prettyPrintArrayToString(indent, (List<Object>) obj);
        } else {
            return obj.toString();
        }
    }

    private static String prettyPrintMapToString(String indent, Map<String, Object> map) {
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
            sb.append(nextIndent).append("\"").append(escapeString(key)).append("\": ");
            sb.append(prettyPrintToString(nextIndent, map.get(key)));
            first = false;
        }

        sb.append("\n").append(indent).append("}");
        return sb.toString();
    }

    private static String prettyPrintArrayToString(String indent, List<Object> array) {
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
            sb.append(prettyPrintToString(nextIndent, element));
            first = false;
        }

        sb.append("\n").append(indent).append("]");
        return sb.toString();
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

    /**
     * Helper method to create a LinkedHashMap with alternating keys and values
     */
    private static Map<String, Object> createMap(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }
}