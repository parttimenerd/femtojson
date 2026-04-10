package me.bechberger.util.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class JSONParser {

    private final Reader reader;
    private int current;
    private int line = 1;
    private int column = 0;

    public JSONParser(InputStream input) throws IOException {
        this(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    public JSONParser(Reader reader) throws IOException {
        this.reader = reader;
        this.current = reader.read();
    }

    public JSONParser(String json) throws IOException {
        this(new StringReader(json));
    }

    public static Object parse(String json) throws IOException {
        JSONParser parser = new JSONParser(json);
        Object value = parser.parseJson();
        parser.parseWs();
        if (parser.current != -1) {
            throw new JSONParseException(parser.line, parser.column,
                    "Unexpected trailing content: '" + (char) parser.current + "'");
        }
        return value;
    }

    /**
     * Expects the current character to be the given expected character, and advances to the next character.
     * @param expected the expected character
     * @throws IOException if an I/O error occurs
     */
    private void expect(char expected) throws IOException {
        if (current != expected) {
            throw new JSONParseException(line, column, "Expected '" + expected + "' but got '" + (char) current + "'");
        }
        advance();
    }

    /**
     * Expects the current position to match the given string, and advances past all characters in the string.
     * @param string the expected string
     * @throws IOException if an I/O error occurs
     */
    private void expect(String string) throws IOException {
        for (char ch : string.toCharArray()) {
            expect(ch);
        }
    }

    /**
     * Advances to the next character and updates line and column numbers.
     * @return the new current character
     * @throws IOException if an I/O error occurs
     */
    private int advance() throws IOException {
        current = reader.read();
        if (current == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
        return current;
    }

    /**
     * Parses a JSON document.
     * <pre>
     * json
     *    element
     * </pre>
     * @return the parsed JSON value (Object, Array, String, Number, Boolean, or null)
     * @throws IOException if an I/O error occurs
     */
    public Object parseJson() throws IOException {
        return parseElement();
    }

    /**
     * <pre>
     * element
     *     ws value ws
     * </pre>
     */
    public Object parseElement() throws IOException {
        parseWs();
        Object value = parseValue();
        parseWs();
        return value;
    }

    /**
     * <pre>
     * value
     *    object
     *    array
     *    string
     *    number
     *    "true"
     *    "false"
     *    "null"
     * </pre>
     */
    public Object parseValue() throws IOException {
        if (current == '{') {
            return parseObject();
        } else if (current == '[') {
            return parseArray();
        } else if (current == '"') {
            return parseString();
        } else if (current == 't' || current == 'f') {
            return parseBoolean();
        } else if (current == 'n') {
            return parseNull();
        } else if (current == '-' || (current >= '0' && current <= '9')) {
            return parseNumber();
        } else {
            throw new JSONParseException(line, column, "Unexpected character '" + (char) current + "'");
        }
    }

    /**
     * Simple class to hold a key-value pair for JSON object members
     */
    static class Member {
        public String key;
        public Object value;

        public Member(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * <pre>
     * object  # a JSON object is either empty ('{ }') or has members
     *     '{' ws '}'
     *     '{' member (',' member)* '}'
     * </pre>
     */
    public Map<String, Object> parseObject() throws IOException {
        expect('{');
        Map<String, Object> map = new HashMap<>();
        parseWs();

        if (current == '}') {
            advance();
            return map;
        }

        while (true) {
            Member member = parseMember();
            map.put(member.key, member.value);
            parseWs();

            if (current == '}') {
                advance();
                break;
            } else if (current == ',') {
                advance();
            } else {
                throw new JSONParseException(line, column, "Expected ',' or '}' but got '" + (char) current + "'");
            }
        }

        return map;
    }

    /**
     * <pre>
     * member  # a member is '"key": value', with arbitrary whitespace
     *     ws string ws ':' element
     * </pre>
     */
    private Member parseMember() throws IOException {
        parseWs();
        String key = parseString();
        parseWs();
        expect(':');
        Object value = parseElement();
        return new Member(key, value);
    }

    /**
     * <pre>
     * array   # an array is either empty or has elements
     *     '[' ws ']'
     *     '[' element (',' elements)* ']'
     * </pre>
     */
    public List<Object> parseArray() throws IOException {
        expect('[');
        List<Object> list = new ArrayList<>();
        parseWs();

        if (current == ']') {
            advance();
            return list;
        }

        while (true) {
            Object element = parseElement();
            list.add(element);
            parseWs();

            if (current == ']') {
                advance();
                break;
            } else if (current == ',') {
                advance();
            } else {
                throw new JSONParseException(line, column, "Expected ',' or ']' but got '" + (char) current + "'");
            }
        }

        return list;
    }

    /**
     * <pre>
     * string  # a string is characters inside '"'
     *     '"' character* '"'
     * </pre>
     */
    public String parseString() throws IOException {
        expect('"');
        StringBuilder sb = new StringBuilder();

        while (current != '"') {
            if (current == -1) {
                throw new JSONParseException(line, column, "Unexpected end of input in string");
            }
            if (current == '\\') {
                parseEscape(sb);
            } else {
                parseCharacter(sb);
            }
        }

        expect('"');
        return sb.toString();
    }

    /**
     * <pre>
     * character # essentially all non control characters excluding '"' and '\'
     *     '0020' . '10FFFF' - '"' - '\'
     *     '\' escape
     * </pre>
     * Handles supplementary characters (e.g. emoji) that are represented
     * as surrogate pairs by Reader.
     */
    private void parseCharacter(StringBuilder sb) throws IOException {
        if (current >= 0x0020 && current != '"' && current != '\\') {
            char hi = (char) current;
            advance();
            if (Character.isHighSurrogate(hi)) {
                // Must be followed by a low surrogate
                if (Character.isLowSurrogate((char) current)) {
                    char lo = (char) current;
                    advance();
                    sb.append(hi);
                    sb.append(lo);
                } else {
                    throw new JSONParseException(line, column,
                            "Expected low surrogate after high surrogate");
                }
            } else {
                sb.append(hi);
            }
        } else {
            throw new JSONParseException(line, column, "Invalid character in string: " + (char) current);
        }
    }

    /**
     * <pre>
     * escape   # the characters that can be escaped + special characters
     *     '"'
     *     '\'
     *     '/'
     *     'b'
     *     'f'
     *     'n'
     *     'r'
     *     't'
     *     'u' hex hex hex hex
     * </pre>
     */
    private void parseEscape(StringBuilder sb) throws IOException {
        expect('\\');
        if (current == '"') {
            advance();
            sb.append('"');
        } else if (current == '\\') {
            advance();
            sb.append('\\');
        } else if (current == '/') {
            advance();
            sb.append('/');
        } else if (current == 'b') {
            advance();
            sb.append('\b');
        } else if (current == 'f') {
            advance();
            sb.append('\f');
        } else if (current == 'n') {
            advance();
            sb.append('\n');
        } else if (current == 'r') {
            advance();
            sb.append('\r');
        } else if (current == 't') {
            advance();
            sb.append('\t');
        } else if (current == 'u') {
            advance();
            int codepoint = parseHexQuad();
            // Handle surrogate pairs: \uD800-\uDBFF followed by \uDC00-\uDFFF
            if (Character.isHighSurrogate((char) codepoint)) {
                if (current == '\\') {
                    advance();
                    if (current == 'u') {
                        advance();
                        int low = parseHexQuad();
                        if (Character.isLowSurrogate((char) low)) {
                            sb.append(Character.toChars(
                                    Character.toCodePoint((char) codepoint, (char) low)));
                        } else {
                            throw new JSONParseException(line, column,
                                    "Expected low surrogate after \\uD83D, got \\u"
                                            + String.format("%04X", low));
                        }
                    } else {
                        throw new JSONParseException(line, column,
                                "Expected \\uXXXX low surrogate after high surrogate");
                    }
                } else {
                    throw new JSONParseException(line, column,
                            "Expected \\uXXXX low surrogate after high surrogate");
                }
            } else if (Character.isLowSurrogate((char) codepoint)) {
                throw new JSONParseException(line, column,
                        "Lone low surrogate \\u" + String.format("%04X", codepoint)
                                + " is not valid");
            } else {
                sb.append(Character.toChars(codepoint));
            }
        } else {
            throw new JSONParseException(line, column, "Invalid escape sequence: \\" + (char) current);
        }
    }

    /**
     * Parse four hex digits and return the resulting integer value.
     */
    private int parseHexQuad() throws IOException {
        int codepoint = 0;
        for (int i = 0; i < 4; i++) {
            codepoint = codepoint * 16 + parseHex();
        }
        return codepoint;
    }

    /**
     * <pre>
     * hex     # valid hexadecimal character
     *     digit
     *     'A' . 'F'
     *     'a' . 'f'
     * </pre>
     */
    private int parseHex() throws IOException {
        if (current >= '0' && current <= '9') {
            int ch = current - '0';
            advance();
            return ch;
        } else if (current >= 'A' && current <= 'F') {
            int ch = current - 'A' + 10;
            advance();
            return ch;
        } else if (current >= 'a' && current <= 'f') {
            int ch = current - 'a' + 10;
            advance();
            return ch;
        } else {
            throw new JSONParseException(line, column, "Expected hex digit but got '" + (char) current + "'");
        }
    }

    /**
     * <pre>
     * number  # numbers a floating points with optional exponents
     *     integer fraction exponent
     * </pre>
     */
    public Double parseNumber() throws IOException {
        StringBuilder sb = new StringBuilder();

        // Parse integer
        parseInteger(sb);
        // Parse fraction
        parseFraction(sb);
        // Parse exponent
        parseExponent(sb);

        String numStr = sb.toString();
        return Double.parseDouble(numStr);
    }

    /**
     * <pre>
     * integer
     *     digit
     *     onenine digits
     *     '-' digit
     *     '-' onenine digits
     * </pre>
     */
    public void parseInteger(StringBuilder sb) throws IOException {
        if (current == '-') {
            sb.append((char) current);
            advance();
        }

        if (current >= '1' && current <= '9') {
            sb.append((char) current);
            advance();
            parseDigits(sb);
        } else if (current == '0') {
            sb.append((char) current);
            advance();
            if (current >= '0' && current <= '9') {
                throw new JSONParseException(line, column, "Leading zeros are not allowed in JSON numbers");
            }
        } else {
            throw new JSONParseException(line, column, "Expected digit but got '" + (char) current + "'");
        }
    }

    /**
     * <pre>
     * digits
     *     digit
     *     digit digits
     * </pre>
     */
    private void parseDigits(StringBuilder sb) throws IOException {
        while (current >= '0' && current <= '9') {
            sb.append((char) current);
            advance();
        }
    }

    /**
     * <pre>
     * digit
     *     '0'
     *     onenine
     * </pre>
     */
    private void parseDigit(StringBuilder sb) throws IOException {
        if (current >= '0' && current <= '9') {
            sb.append((char) current);
            advance();
        } else {
            throw new JSONParseException(line, column, "Expected digit but got '" + (char) current + "'");
        }
    }

    /**
     * <pre>
     * fraction
     *     ""
     *     '.' digits
     * </pre>
     */
    public void parseFraction(StringBuilder sb) throws IOException {
        if (current == '.') {
            sb.append((char) current);
            advance();
            if (current >= '0' && current <= '9') {
                parseDigits(sb);
            } else {
                throw new JSONParseException(line, column, "Expected digit after '.' but got '" + (char) current + "'");
            }
        }
    }

    /**
     * <pre>
     * exponent
     *     ""
     *     'E' sign digits
     *     'e' sign digits
     * </pre>
     */
    private void parseExponent(StringBuilder sb) throws IOException {
        if (current == 'E' || current == 'e') {
            sb.append((char) current);
            advance();
            parseSign(sb);
            if (current >= '0' && current <= '9') {
                parseDigits(sb);
            } else {
                throw new JSONParseException(line, column, "Expected digit in exponent but got '" + (char) current + "'");
            }
        }
    }

    /**
     * <pre>
     * sign
     *     ""
     *     '+'
     *     '-'
     * </pre>
     */
    private void parseSign(StringBuilder sb) throws IOException {
        if (current == '+' || current == '-') {
            sb.append((char) current);
            advance();
        }
    }

    /**
     * <pre>
     * ws   # all supported whitespace characters (as hex codepoints)
     *     ""
     *     '0020' ws
     *     '000A' ws
     *     '000D' ws
     *     '0009' ws
     * </pre>
     */
    private void parseWs() throws IOException {
        while (current == 0x0020 || current == 0x000A || current == 0x000D || current == 0x0009) {
            advance();
        }
    }

    /**
     * <pre>
     * value = "true" | "false"
     * </pre>
     */
    private Boolean parseBoolean() throws IOException {
        if (current == 't') {
            expect("true");
            return true;
        } else if (current == 'f') {
            expect("false");
            return false;
        } else {
            throw new JSONParseException(line, column, "Expected 'true' or 'false'");
        }
    }

    /**
     * <pre>
     * value = "null"
     * </pre>
     */
    private Object parseNull() throws IOException {
        expect("null");
        return null;
    }
}