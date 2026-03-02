package me.bechberger.util.json;

/**
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
