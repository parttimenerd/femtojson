package me.bechberger.util.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for confirmed bugs in the JSON parser.
 *
 * Bug 1: Trailing content after the first JSON value is silently ignored.
 *        parseJson()/parseElement() never checks that all input has been consumed.
 *
 * Bug 2: Leading zeros in numbers (00, 01, 007, -01, …) are accepted.
 *        JSON spec (RFC 8259 §6) forbids leading zeros.
 *
 * Bug 3: Lone low surrogate escape sequences (\uDC00) are accepted.
 *        These are invalid Unicode scalar values and should be rejected.
 */
public class BugFinderTest {

    // ════════════════════════════════════════════════════════════════════
    // Bug 1: Trailing content silently ignored
    //   parseJson() → parseElement() returns after parsing the first value
    //   without verifying that the remaining input is only whitespace/EOF.
    // ════════════════════════════════════════════════════════════════════

    @ParameterizedTest(name = "trailing content should fail: {0}")
    @ValueSource(strings = {
        "123 456",          // two numbers separated by space
        "true false",       // two booleans
        "null null",        // two nulls
        "[1,2] [3,4]",     // two arrays
        "{} {}",            // two objects
        "\"a\" \"b\"",      // two strings
        "true,",            // trailing comma after valid value
        "null!",            // trailing non-whitespace garbage
        "1 2 3",            // three numbers
    })
    void trailingContentShouldFail(String json) {
        assertThrows(Exception.class, () -> JSONParser.parse(json),
            "Should reject trailing content: " + json);
    }

    @Test
    void trailingContentInObject() {
        assertThrows(Exception.class,
            () -> JSONParser.parse("{\"a\":1} extra"),
            "Should reject content after closing brace");
    }

    @Test
    void trailingContentInArray() {
        assertThrows(Exception.class,
            () -> JSONParser.parse("[1,2,3] extra"),
            "Should reject content after closing bracket");
    }

    // ════════════════════════════════════════════════════════════════════
    // Bug 2: Leading zeros accepted in numbers
    //   parseInteger() handles '0' by consuming it and returning,
    //   without checking that the next character is NOT a digit.
    //   Combined with Bug 1, "007" silently parses as 0.0.
    // ════════════════════════════════════════════════════════════════════

    @ParameterizedTest(name = "leading zeros should fail: {0}")
    @ValueSource(strings = {
        "00",       // two zeros
        "01",       // zero then digit
        "007",      // multiple digits after zero
        "00.5",     // zero prefix then fraction
        "-00",      // negative two zeros
        "-01",      // negative with leading zero
        "-007",     // negative multiple digits after zero
    })
    void leadingZerosShouldFail(String json) {
        assertThrows(Exception.class, () -> JSONParser.parse(json),
            "Should reject leading zeros: " + json);
    }

    // ════════════════════════════════════════════════════════════════════
    // Bug 3: Lone low surrogate escape accepted
    //   parseEscape() handles backslash-u XXXX: it checks if the decoded
    //   value is a *high* surrogate and requires a following low surrogate,
    //   but it does NOT check for a *lone low* surrogate (U+DC00 to U+DFFF).
    //   A bare backslash-uDC00 is silently accepted, producing an invalid
    //   Java String containing an unpaired surrogate.
    // ════════════════════════════════════════════════════════════════════

    @Test
    void loneLowSurrogateShouldFail() {
        // backslash-uDC00 is a low surrogate without preceding high surrogate
        String json = "\"" + '\\' + "uDC00\"";
        assertThrows(Exception.class, () -> JSONParser.parse(json),
            "Should reject lone low surrogate");
    }

    @Test
    void loneLowSurrogateMiddleOfStringShouldFail() {
        // Lone low surrogate embedded in otherwise valid text
        String json = "\"abc" + '\\' + "uDC00def\"";
        assertThrows(Exception.class, () -> JSONParser.parse(json),
            "Should reject lone low surrogate in middle of string");
    }

    @Test
    void loneLowSurrogateVariantsShouldFail() {
        // U+DFFF is also a low surrogate
        String json = "\"" + '\\' + "uDFFF\"";
        assertThrows(Exception.class, () -> JSONParser.parse(json),
            "Should reject lone low surrogate U+DFFF");
    }
}
