package me.yz.yzjson;

public class WhitespaceParser {

    private static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    public static int parse(final CharSequence sequence, final int index) {
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i);
            if (!isWhitespace(c)) {
                break;
            }
            ++i;
        }
        return i;
    }
}
