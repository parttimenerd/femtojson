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
                Arguments.of(
                        new ArrayList<>(Arrays.asList(1.0, "two", true, null)),
                        "[1.0,\"two\",true,null]"
                ),

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
                )
        );
    }

    @ParameterizedTest(name = "[{index}]")
    @MethodSource("compactPrintCases")
    public void testCompactPrint(Object obj, String expected) {
        String actual = CompactPrinter.compactPrint(obj);
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

    private static Map<String, Object> createMap(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }
}
