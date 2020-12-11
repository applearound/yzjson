package me.yz.yzjson;

public class StringParser {

    static class PartialResult {
        private final int index;
        private final String result;

        public PartialResult(final int index, final String result) {
            this.index = index;
            this.result = result;
        }

        public int getIndex() {
            return index;
        }

        public String getResult() {
            return result;
        }
    }

    static class UnicodeParticalResult {
        private final int index;
        private final char result;

        public UnicodeParticalResult(final int index, final char result) {
            this.index = index;
            this.result = result;
        }

        public int getIndex() {
            return index;
        }

        public char getResult() {
            return result;
        }
    }

    private enum Status {
        INIT,
        READ_STRING,
        END,
        ESCAPE_CHARACTER,
        UNICODE_0,
        UNICODE_1,
        UNICODE_2,
        UNICODE_3
    }

    private static boolean isDoubleQuotationMark(final char c) {
        return c == '"';
    }

    private static boolean isReverseSolidus(final char c) {
        return c == '\\';
    }

    private static boolean isControl(final char c) {
        return c <= 0x001F || c == 0x007F || (0x0080 <= c && c <= 0x009F);
    }

    private static boolean isHexDigit(final char c) {
        return ('0' <= c && c <= '9') || ('A' <= c && c <= 'F') || ('a' <= c && c <= 'f');
    }

    private static int hexToDecimalInt(final char c) {
        if ('0' <= c && c <= '9') {
            return c - 48;
        } else if ('A' <= c && c <= 'F') {
            return c - 55;
        } else if ('a' <= c && c <= 'f') {
            return c - 87;
        } else {
            throw new IllegalArgumentException("c");
        }
    }

    private static boolean isSolidus(final char c) {
        return c == '/';
    }

    private static boolean isBackSpace(final char c) {
        return c == 'b';
    }

    private static boolean isFormfeed(final char c) {
        return c == 'f';
    }

    private static boolean isLineFeed(final char c) {
        return c == 'n';
    }

    private static boolean isCarriageReturn(final char c) {
        return c == 'r';
    }

    private static boolean isHorizontalTab(final char c) {
        return c == 't';
    }

    private static boolean isUnicode(final char c) {
        return c == 'u';
    }

    private static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    public static PartialResult parse(final CharSequence sequence, final int index) {
        final StringBuilder resultStringBuilder = new StringBuilder();

        Status currentStatus = Status.INIT;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case INIT:
                    if (isDoubleQuotationMark(c)) {
                        currentStatus = Status.READ_STRING;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case READ_STRING:
                    if (isDoubleQuotationMark(c)) {
                        currentStatus = Status.END;
                    } else if (isReverseSolidus(c)) {
                        currentStatus = Status.ESCAPE_CHARACTER;
                    } else if (!isControl(c)) {
                        resultStringBuilder.append(c);
                    } else {
                        throw new ParseException();
                    }
                    break;
                case ESCAPE_CHARACTER:
                    if (isDoubleQuotationMark(c) ||
                            isReverseSolidus(c) ||
                            isSolidus(c) ||
                            isBackSpace(c) ||
                            isFormfeed(c) ||
                            isLineFeed(c) ||
                            isCarriageReturn(c) ||
                            isHorizontalTab(c)) {
                        currentStatus = Status.READ_STRING;
                    } else if (isUnicode(c)) {
                        final UnicodeParticalResult unicodeParticalResult = parseUnicode(sequence, i);
                        i = unicodeParticalResult.getIndex();
                        currentStatus = Status.UNICODE_0;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case END:
                    return new PartialResult(i - 1, resultStringBuilder.toString());
                default:
                    throw new ParseException();
            }
        }
        if (currentStatus != Status.END) {
            throw new ParseException();
        }
        return new PartialResult(i, resultStringBuilder.toString());
    }

    public static UnicodeParticalResult parseUnicode(final CharSequence sequence, final int index) {
        if (sequence.length() - index < 4) {
            throw new ParseException();
        } else {
            char codepoint = 0;
            int shift = 12;
            for (int i = 0; i < 4; i++) {
                final char c = sequence.charAt(index + i);
                codepoint += c << shift;
                shift -= 4;
            }
            return new UnicodeParticalResult(index + 4, codepoint);
        }
    }
}
