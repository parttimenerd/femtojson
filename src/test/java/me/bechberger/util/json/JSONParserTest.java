package me.bechberger.util.json;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JSONParserTest {

    /**
     * Test data: pairs of (JSON string, expected parsed object)
     */
    static Stream<Arguments> jsonParsingCases() {
        return Stream.of(
                // Null values
                Arguments.of("null", null),

                // Booleans
                Arguments.of("true", true),
                Arguments.of("false", false),

                // Numbers - all as Double
                Arguments.of("0", 0.0),
                Arguments.of("42", 42.0),
                Arguments.of("-1", -1.0),
                Arguments.of("3.14", 3.14),
                Arguments.of("-2.5", -2.5),
                Arguments.of("1e10", 1e10),
                Arguments.of("1.5e-2", 1.5e-2),

                // Strings
                Arguments.of("\"hello\"", "hello"),
                Arguments.of("\"\"", ""),
                Arguments.of("\"hello world\"", "hello world"),
                Arguments.of("\"\\\"quoted\\\"\"", "\"quoted\""),
                Arguments.of("\"line1\\nline2\"", "line1\nline2"),
                Arguments.of("\"tab\\there\"", "tab\there"),

                // Empty arrays and objects
                Arguments.of("[]", new ArrayList<>()),
                Arguments.of("{}", new HashMap<>()),
                Arguments.of("[ ]", new ArrayList<>()),
                Arguments.of("{ }", new HashMap<>()),

                // Simple array
                Arguments.of(
                        "[1, 2, 3]",
                        new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0))
                ),

                // Simple object
                Arguments.of(
                        "{\"a\": 1}",
                        createMap("a", 1.0)
                ),

                // Mixed array
                Arguments.of(
                        "[1, \"two\", true, null]",
                        new ArrayList<>(Arrays.asList(1.0, "two", true, null))
                ),

                // Nested object
                Arguments.of(
                        "{\"a\": {\"b\": 2}}",
                        createMap("a", createMap("b", 2.0))
                ),

                // Nested array
                Arguments.of(
                        "[[1, 2], [3, 4]]",
                        new ArrayList<>(Arrays.asList(
                                new ArrayList<>(Arrays.asList(1.0, 2.0)),
                                new ArrayList<>(Arrays.asList(3.0, 4.0))
                        ))
                ),

                // Complex nested structure
                Arguments.of(
                        "{\"items\": [1, {\"key\": \"value\"}], \"count\": 2}",
                        createMap(
                                "items", new ArrayList<>(Arrays.asList(
                                        1.0,
                                        createMap("key", "value")
                                )),
                                "count", 2.0
                        )
                ),

                // Object with empty nested structures
                Arguments.of(
                        "{\"a\": {}, \"b\": []}",
                        createMap("a", new HashMap<>(), "b", new ArrayList<>())
                ),

                // Whitespace handling
                Arguments.of(
                        "  {  \"a\"  :  1  }  ",
                        createMap("a", 1.0)
                ),

                // Multiple fields in object
                Arguments.of(
                        "{\"a\": 1, \"b\": 2, \"c\": 3}",
                        createMap("a", 1.0, "b", 2.0, "c", 3.0)
                ),

                // Edge cases: Numbers
                Arguments.of("0", 0.0),
                Arguments.of("1", 1.0),
                Arguments.of("999", 999.0),
                Arguments.of("-0", -0.0),
                Arguments.of("-999", -999.0),
                Arguments.of("0.0", 0.0),
                Arguments.of("0.1", 0.1),
                Arguments.of("-0.1", -0.1),
                Arguments.of("1.23e4", 1.23e4),
                Arguments.of("1.23E4", 1.23E4),
                Arguments.of("1.23e+4", 1.23e+4),
                Arguments.of("1.23e-4", 1.23e-4),
                Arguments.of("-1.23e4", -1.23e4),

                // Edge cases: Strings with escape sequences
                Arguments.of("\"\\n\"", "\n"),
                Arguments.of("\"\\r\"", "\r"),
                Arguments.of("\"\\t\"", "\t"),
                Arguments.of("\"\\b\"", "\b"),
                Arguments.of("\"\\f\"", "\f"),
                Arguments.of("\"\\\\\"", "\\"),
                Arguments.of("\"\\/\"", "/"),
                Arguments.of("\"\\\"\"", "\""),
                Arguments.of("\"\\u0041\"", "A"),
                Arguments.of("\"\\u0031\"", "1"),
                Arguments.of("\"\\u00e9\"", "é"),
                Arguments.of("\"mixed\\ntext\\t\\\"quoted\\\"\"", "mixed\ntext\t\"quoted\""),

                // Edge cases: Empty and single-element collections
                Arguments.of("[ ]", new ArrayList<>()),
                Arguments.of("[ null ]", new ArrayList<>(Collections.singletonList(null))),
                Arguments.of("[ true ]", new ArrayList<>(Collections.singletonList(true))),
                Arguments.of("[ 0 ]", new ArrayList<>(Collections.singletonList(0.0))),
                Arguments.of("[ \"\" ]", new ArrayList<>(Collections.singletonList(""))),

                // Edge cases: Objects with single field
                Arguments.of("{ \"key\": null }", createMap("key", null)),
                Arguments.of("{ \"key\": true }", createMap("key", true)),
                Arguments.of("{ \"key\": false }", createMap("key", false)),
                Arguments.of("{ \"x\": 0 }", createMap("x", 0.0)),
                Arguments.of("{ \"empty\": \"\" }", createMap("empty", "")),

                // Edge cases: Deeply nested structures (3 levels)
                Arguments.of(
                        "{\"a\": {\"b\": {\"c\": 1}}}",
                        createMap("a", createMap("b", createMap("c", 1.0)))
                ),
                Arguments.of(
                        "[[[1]]]",
                        new ArrayList<>(Collections.singletonList(
                                new ArrayList<>(Collections.singletonList(
                                        new ArrayList<>(Collections.singletonList(1.0))
                                ))
                        ))
                ),

                // Edge cases: Mixed deeply nested
                Arguments.of(
                        "{\"arr\": [{\"obj\": [1, 2, 3]}]}",
                        createMap("arr", new ArrayList<>(Collections.singletonList(
                                createMap("obj", new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)))
                        )))
                ),

                // Edge cases: Arrays with mixed types
                Arguments.of(
                        "[null, true, false, 0, \"\", [], {}]",
                        new ArrayList<>(Arrays.asList(null, true, false, 0.0, "", new ArrayList<>(), new HashMap<>()))
                ),

                // Edge cases: Objects with many fields
                Arguments.of(
                        "{\"a\": 1, \"b\": 2, \"c\": 3, \"d\": 4, \"e\": 5}",
                        createMap("a", 1.0, "b", 2.0, "c", 3.0, "d", 4.0, "e", 5.0)
                ),

                // Edge cases: Long strings
                Arguments.of(
                        "\"" + "x".repeat(100) + "\"",
                        "x".repeat(100)
                ),
                Arguments.of(
                        "\"" + "abc".repeat(50) + "\"",
                        "abc".repeat(50)
                ),

                // Edge cases: Special string contents
                Arguments.of("\"   \"", "   "),
                Arguments.of("\"\\n\\n\\n\"", "\n\n\n"),
                Arguments.of("\"\\t\\t\"", "\t\t"),
                Arguments.of("\"a\\\\b\"", "a\\b"),
                Arguments.of("\"\\\"\\\"\"", "\"\""),

                // Edge cases: Numbers at boundaries
                Arguments.of("2147483647", 2147483647.0),
                Arguments.of("-2147483648", -2147483648.0),
                Arguments.of("0.00000001", 0.00000001),
                Arguments.of("999999999999.999999", 999999999999.999999),

                // Edge cases: Unicode escape sequences in strings and keys
                Arguments.of(
                        "{\"\\u0041\": 1}",
                        createMap("A", 1.0)
                ),
                Arguments.of(
                        "{\"\\u0042\\u0043\": \"value\"}",
                        createMap("BC", "value")
                ),

                // Edge cases: Arrays with null elements
                Arguments.of(
                        "[null, null, null]",
                        new ArrayList<>(Arrays.asList(null, null, null))
                ),

                // Edge cases: Objects with complex nesting patterns
                Arguments.of(
                        "{\"x\": [{}, {}, {}]}",
                        createMap("x", new ArrayList<>(Arrays.asList(new HashMap<>(), new HashMap<>(), new HashMap<>())))
                ),
                Arguments.of(
                        "{\"x\": [[], [], []]}",
                        createMap("x", new ArrayList<>(Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>())))
                ),

                // Edge cases: Whitespace variations
                Arguments.of(
                        "\n\t{  \n\"a\":1\n,\n\"b\":2\n}\n\t",
                        createMap("a", 1.0, "b", 2.0)
                ),
                Arguments.of(
                        "[\n1,\n2,\n3\n]",
                        new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0))
                ),

                // Edge cases: Scientific notation variations
                Arguments.of("0e0", 0.0),
                Arguments.of("1e0", 1.0),
                Arguments.of("1.5e0", 1.5),
                Arguments.of("1e1", 10.0),
                Arguments.of("1e-1", 0.1),
                Arguments.of("5e2", 500.0)
        );
    }

    /**
     * Test that JSON strings are parsed correctly into expected objects
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("jsonParsingCases")
    public void testParseJSON(String json, Object expected) throws IOException {
        JSONParser parser = new JSONParser(json);
        Object parsed = parser.parseJson();
        assertEquals(expected, parsed, "Parsed object should match expected value for JSON: " + json);
    }

    /**
     * Test that parse -> prettyPrint -> parse yields the same object
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("jsonParsingCases")
    public void testRoundtrip(String json, Object expected) throws IOException {
        // Parse original JSON
        JSONParser parser1 = new JSONParser(json);
        Object parsed1 = parser1.parseJson();
        assertEquals(expected, parsed1, "Initial parse failed");

        // Pretty print to string
        String prettyPrinted = prettyPrintToString(parsed1);

        // Parse the pretty-printed JSON
        JSONParser parser2 = new JSONParser(prettyPrinted);
        Object parsed2 = parser2.parseJson();

        // Should be equal to the original parsed object
        assertEquals(parsed1, parsed2, "Roundtrip (parse -> prettyPrint -> parse) should preserve the object");
    }

    /**
     * Helper method to pretty print an object to a string instead of System.out
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
        } else if (obj instanceof ArrayList) {
            return prettyPrintArrayToString(indent, (ArrayList<Object>) obj);
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

    private static String prettyPrintArrayToString(String indent, ArrayList<Object> array) {
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
     * Helper method to create a HashMap with alternating keys and values
     */
    private static Map<String, Object> createMap(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }
}