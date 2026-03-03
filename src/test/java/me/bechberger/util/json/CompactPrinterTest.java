package me.bechberger.util.json;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CompactPrinterTest {

    /**
<<<<<<< HEAD
     * Test data: pairs of (object, expected compact JSON string)
     */
    static Stream<Arguments> compactPrintCases() {
        return Stream.of(
                // Primitives
                Arguments.of(null, "null"),
                Arguments.of(true, "true"),
                Arguments.of(false, "false"),
                Arguments.of(0, "0"),
                Arguments.of(42, "42"),
                Arguments.of(0.0, "0.0"),
                Arguments.of(3.14, "3.14"),

                // Simple strings
                Arguments.of("hello", "\"hello\""),
                Arguments.of("", "\"\""),
                Arguments.of("a\"b", "\"a\\\"b\""),
                Arguments.of("a\\b", "\"a\\\\b\""),
                Arguments.of("line\nnewline", "\"line\\nnewline\""),

                // Empty collections
                Arguments.of(new HashMap<>(), "{}"),
                Arguments.of(new ArrayList<>(), "[]"),

                // Simple object
                Arguments.of(
                        createMap("a", 1),
                        "{\"a\":1}"
                ),

                // Simple array
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1, 2, 3)),
                        "[1,2,3]"
                ),

                // Mixed array
=======
     * Test data: pairs of (object to compact print, expected compact output)
     */
    static Stream<Arguments> compactPrintCases() {
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
                        "[1.0]"
                ),

                // Single-element object
                Arguments.of(
                        createMap("key", "value"),
                        "{\"key\":\"value\"}"
                ),

                // Multi-element array
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)),
                        "[1.0,2.0,3.0]"
                ),

                // Multi-element object
                Arguments.of(
                        createMap("a", 1.0, "b", 2.0),
                        "{\"a\":1.0,\"b\":2.0}"
                ),

                // Array with strings
                Arguments.of(
                        new ArrayList<>(Arrays.asList("foo", "bar", "baz")),
                        "[\"foo\",\"bar\",\"baz\"]"
                ),

                // Array with mixed types
>>>>>>> 0bfff6c (Add compact printer)
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1.0, "two", true, null)),
                        "[1.0,\"two\",true,null]"
                ),

<<<<<<< HEAD
                // Nested
                Arguments.of(
                        createMap("a", createMap("b", 2)),
                        "{\"a\":{\"b\":2}}"
                ),

                // ── Unicode / emoji ──

                // Emoji string (escaped as surrogate pair)
                Arguments.of(
                        "😀",
                        "\"\\ud83d\\ude00\""
                ),

                // Non-ASCII BMP
                Arguments.of(
                        "café",
                        "\"caf\\u00e9\""
                ),

                // CJK
                Arguments.of(
                        "日本語",
                        "\"\\u65e5\\u672c\\u8a9e\""
                ),

                // Multiple emoji
                Arguments.of(
                        "🎉🥳🎊",
                        "\"\\ud83c\\udf89\\ud83e\\udd73\\ud83c\\udf8a\""
                ),

                // Emoji in object key
                Arguments.of(
                        createMap("😀", "grin"),
                        "{\"\\ud83d\\ude00\":\"grin\"}"
                ),

                // Emoji in array
                Arguments.of(
                        new ArrayList<>(Arrays.asList("😀", "🌍")),
                        "[\"\\ud83d\\ude00\",\"\\ud83c\\udf0d\"]"
=======
                // Nested array
                Arguments.of(
                        new ArrayList<>(Arrays.asList(
                                new ArrayList<>(Arrays.asList(1.0, 2.0)),
                                new ArrayList<>(Arrays.asList(3.0, 4.0))
                        )),
                        "[[1.0,2.0],[3.0,4.0]]"
                ),

                // Nested object
                Arguments.of(
                        createMap("outer", createMap("inner", "value")),
                        "{\"outer\":{\"inner\":\"value\"}}"
                ),

                // Complex nested structure
                Arguments.of(
                        createMap("items", new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "nested", createMap("key", "value")),
                        "{\"items\":[1.0,2.0,3.0],\"nested\":{\"key\":\"value\"}}"
                ),

                // Object with empty nested structures
                Arguments.of(
                        createMap("empty_obj", new HashMap<>(), "empty_arr", new ArrayList<>()),
                        "{\"empty_obj\":{},\"empty_arr\":[]}"
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
                        "[[],{}]"
                ),

                // Deeply nested structure (3 levels)
                Arguments.of(
                        createMap("a", createMap("b", createMap("c", 1.0))),
                        "{\"a\":{\"b\":{\"c\":1.0}}}"
                ),

                // Array with nested objects
                Arguments.of(
                        new ArrayList<>(Arrays.asList(
                                createMap("id", 1.0),
                                createMap("id", 2.0)
                        )),
                        "[{\"id\":1.0},{\"id\":2.0}]"
>>>>>>> 0bfff6c (Add compact printer)
                )
        );
    }

<<<<<<< HEAD
=======
    /**
     * Test that objects are compact printed with correct formatting
     */
>>>>>>> 0bfff6c (Add compact printer)
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrint(Object obj, String expected) {
        String actual = CompactPrinter.compactPrint(obj);
<<<<<<< HEAD
        assertEquals(expected, actual, "Compact printed output should match expected");
    }

    /**
     * Compact-printed JSON should round-trip through the parser.
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrintRoundtrip(Object obj, String expected) throws IOException {
        String compacted = CompactPrinter.compactPrint(obj);
        Object parsed = JSONParser.parse(compacted);
        // Integers become Doubles after parsing through JSON
        assertEquals(normalizeNumbers(obj), parsed,
                "Compact print → parse round-trip should preserve the object");
    }

    /**
     * Convert Integers to Doubles within a structure (since JSONParser always produces Doubles).
     */
    @SuppressWarnings("unchecked")
    private static Object normalizeNumbers(Object obj) {
        if (obj == null || obj instanceof Boolean || obj instanceof String || obj instanceof Double) {
            return obj;
        } else if (obj instanceof Integer i) {
            return (double) i;
        } else if (obj instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (var entry : map.entrySet()) {
                result.put((String) entry.getKey(), normalizeNumbers(entry.getValue()));
            }
            return result;
        } else if (obj instanceof List<?> list) {
            List<Object> result = new ArrayList<>();
            for (Object item : list) {
                result.add(normalizeNumbers(item));
            }
            return result;
        }
        return obj;
    }

=======
        assertEquals(expected, actual, "Compact printed output should match expected format");
    }

    /**
     * Test that compact printed output is valid JSON that can be parsed back
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrintIsValidJSON(Object obj, String expected) throws IOException {
        String compactPrinted = CompactPrinter.compactPrint(obj);

        // Should not throw an exception when parsing
        JSONParser parser = new JSONParser(compactPrinted);
        Object parsed = parser.parseJson();

        assertEquals(obj, parsed, "Compact printed JSON should parse back to the same object");
    }

    /**
     * Test that compact print produces smaller output than pretty print
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrintIsSmallerThanPrettyPrint(Object obj, String expected) {
        // Skip for simple values where they're the same size
        if (!(obj instanceof Map || obj instanceof List)) {
            return;
        }

        String compact = CompactPrinter.compactPrint(obj);
        String pretty = PrettyPrinter.prettyPrint(obj);

        assertTrue(compact.length() <= pretty.length(),
                "Compact print should be smaller or equal to pretty print");
    }

    /**
     * Helper method to create a LinkedHashMap with alternating keys and values
     */
>>>>>>> 0bfff6c (Add compact printer)
    private static Map<String, Object> createMap(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 0bfff6c (Add compact printer)
