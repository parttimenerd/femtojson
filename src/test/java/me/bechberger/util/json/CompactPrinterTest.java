package me.bechberger.util.json;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CompactPrinterTest {

    /**
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
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1.0, "two", true, null)),
                        "[1.0,\"two\",true,null]"
                ),

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
                )
        );
    }

    /**
     * Test that objects are compact printed with correct formatting
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrint(Object obj, String expected) {
        String actual = PrettyPrinter.compactPrint(obj);
        assertEquals(expected, actual, "Compact printed output should match expected format");
    }

    /**
     * Test that compact printed output is valid JSON that can be parsed back
     */
    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrintIsValidJSON(Object obj, String expected) throws IOException {
        String compactPrinted = PrettyPrinter.compactPrint(obj);

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

        String compact = PrettyPrinter.compactPrint(obj);
        String pretty = PrettyPrinter.prettyPrint(obj);

        assertTrue(compact.length() <= pretty.length(),
                "Compact print should be smaller or equal to pretty print");
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