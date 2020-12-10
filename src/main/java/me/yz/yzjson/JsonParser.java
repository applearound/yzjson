package me.yz.yzjson;

public class JsonParser {

    private enum Status {
        INIT,
        OBJECT,
        COLON,
        COMMA,
        VALUE,
        ARRAY,
        END;
    }

    private static boolean isLeftBrace(final char c) {
        return c == '{';
    }

    private static boolean isRightBrace(final char c) {
        return c == '}';
    }

    private static boolean isLeftBracket(final char c) {
        return c == '[';
    }

    private static boolean isRightBracket(final char c) {
        return c == ']';
    }

    private static boolean isQuotationMark(final char c) {
        return c == '"';
    }

    private static boolean isColon(final char c) {
        return c == ':';
    }

    private static boolean isComma(final char c) {
        return c == ',';
    }

    private static boolean isNumberValue(final char c) {
        return c == '-' || ('0' <= c && c <= '9');
    }

    private static boolean isTureValue(final char c) {
        return c == 't';
    }

    private static boolean isFalseValue(final char c) {
        return c == 't';
    }

    private static boolean isNullValue(final char c) {
        return c == 'n';
    }

    private static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    public static int parseObject(final CharSequence sequence, final int index) {
        Status currentStatus = Status.INIT;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case INIT:
                    if (isLeftBrace(c)) {
                        currentStatus = Status.OBJECT;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case OBJECT:
                    if (isQuotationMark(c)) {
                        i = StringParser.parse(sequence, i);
                        currentStatus = Status.COLON;
                    } else if (isRightBrace(c)) {
                        currentStatus = Status.END;
                    } else if (isWhitespace(c)) {
                        i = parseWhitespace(sequence, i);
                    } else {
                        throw new ParseException();
                    }
                    break;
                case COLON:
                    if (isColon(c)) {
                        currentStatus = Status.VALUE;
                    } else if (!isWhitespace(c)) {
                        throw new ParseException();
                    }
                    break;
                case VALUE:
                    if (isQuotationMark(c)) {
                        i = parseStringValue(sequence, i);
                        currentStatus = Status.COMMA;
                    } else if (isNumberValue(c)) {
                        i = parseNumber(sequence, i);
                        currentStatus = Status.COMMA;
                    } else if (isLeftBrace(c)) {
                        i = parseObject(sequence, i);
                        currentStatus = Status.COMMA;
                    } else if (isLeftBracket(c)) {
                        i = parseArray(sequence, i);
                        currentStatus = Status.COMMA;
                    } else if (isTureValue(c)) {
                        i = parseTrue(sequence, i);
                        currentStatus = Status.COMMA;
                    } else if (isFalseValue(c)) {
                        i = parseFalse(sequence, i);
                        currentStatus = Status.COMMA;
                    } else if (isNullValue(c)) {
                        i = parseNull(sequence, i);
                        currentStatus = Status.COMMA;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case COMMA:
                    if (isComma(c)) {
                        currentStatus = Status.OBJECT;
                    } else {
                        throw new ParseException();
                    }
                default:
                    throw new ParseException();
            }
        }

        return i;
    }

    public static int parseWhitespace(final CharSequence sequence, final int index) {
        return WhitespaceParser.parse(sequence, index);
    }

    public static int parseStringValue(final CharSequence sequence, final int index) {
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            if (isWhitespace(c)) {
                i = WhitespaceParser.parse(sequence, index);
            } else if (c == '"') {
                i = StringParser.parse(sequence, index);
            } else {
                return i - 1;
            }
        }
        return i;
    }

    public static int parseNumber(final CharSequence sequence, final int index) {
        return NumberParser.parse(sequence, index);
    }
}
