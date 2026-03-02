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
                ),

                // ── Unicode / emoji handling ──

                // Emoji in string value (escaped as surrogate pair)
                Arguments.of(
                        "😀",
                        "\"\\ud83d\\ude00\""
                ),

                // Multiple emoji
                Arguments.of(
                        "🎉🥳🎊",
                        "\"\\ud83c\\udf89\\ud83e\\udd73\\ud83c\\udf8a\""
                ),

                // Non-ASCII Latin (BMP, single \\uXXXX)
                Arguments.of(
                        "café",
                        "\"caf\\u00e9\""
                ),

                // CJK characters
                Arguments.of(
                        "日本語",
                        "\"\\u65e5\\u672c\\u8a9e\""
                ),

                // Emoji in object key
                Arguments.of(
                        createMap("😀", "grin"),
                        "{\n  \"\\ud83d\\ude00\": \"grin\"\n}"
                ),

                // Emoji in array
                Arguments.of(
                        new ArrayList<>(Arrays.asList("😀", "🌍")),
                        "[\n  \"\\ud83d\\ude00\",\n  \"\\ud83c\\udf0d\"\n]"
                )
        );
    }

    /**
     * Test that objects are pretty printed with correct formatting
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("prettyPrintCases")
    public void testPrettyPrint(Object obj, String expected) {
        String actual = PrettyPrinter.prettyPrint(obj);
        assertEquals(expected, actual, "Pretty printed output should match expected format");
    }

    /**
     * Test that pretty printed output is valid JSON that can be parsed back
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("prettyPrintCases")
    public void testPrettyPrintIsValidJSON(Object obj, String expected) throws IOException {
        String prettyPrinted = PrettyPrinter.prettyPrint(obj);

        // Should not throw an exception when parsing
        JSONParser parser = new JSONParser(prettyPrinted);
        Object parsed = parser.parseJson();

        assertEquals(obj, parsed, "Pretty printed JSON should parse back to the same object");
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