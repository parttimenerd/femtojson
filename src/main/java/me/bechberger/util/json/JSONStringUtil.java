package me.bechberger.util.json;

/**
<<<<<<< HEAD
 * Shared utility for escaping Java strings into JSON string content.
 * <p>
 * Properly handles supplementary Unicode characters (emoji, CJK extensions, etc.)
 * by emitting surrogate-pair escape sequences.
 */
public final class JSONStringUtil {

    private JSONStringUtil() {
    }

    /**
     * Escape a Java string for inclusion inside JSON double-quotes.
     * <p>
     * Control characters and non-ASCII characters are escaped as unicode escape sequences.
     * Supplementary characters (codepoint &gt; U+FFFF) are escaped as a surrogate pair.
     *
     * @param str the raw Java string
     * @return the escaped string (without surrounding quotes)
     */
    public static String escapeString(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        int i = 0;
        while (i < str.length()) {
            int cp = str.codePointAt(i);
            switch (cp) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (cp < 0x0020) {
                        // Control character
                        sb.append(String.format("\\u%04x", cp));
                    } else if (cp > 0xFFFF) {
                        // Supplementary character → surrogate pair
                        char hi = Character.highSurrogate(cp);
                        char lo = Character.lowSurrogate(cp);
                        sb.append(String.format("\\u%04x\\u%04x", (int) hi, (int) lo));
                    } else if (cp > 0x007E) {
                        // Non-ASCII BMP character
                        sb.append(String.format("\\u%04x", cp));
                    } else {
                        sb.append((char) cp);
                    }
                }
            }
            i += Character.charCount(cp);
        }
        return sb.toString();
    }
}
=======
 * Utility class for JSON string operations
 */
public class JSONStringUtil {

    /**
     * Escapes a string for use in JSON, converting special characters to escape sequences
     * and non-ASCII characters to Unicode escape sequences.
     *
     * @param str the string to escape
     * @return the escaped string
     */
    public static String escapeString(String str) {
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
>>>>>>> 0bfff6c (Add compact printer)
