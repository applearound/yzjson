package me.yz.yzjson;

public class StringParser {
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

    public static int parse(final CharSequence sequence, final int index) {
        Status currentStatus = Status.INIT;
        int i = index;
        char currentChar;
        while (i != sequence.length()) {
            currentChar = sequence.charAt(i);
            switch (currentStatus) {
                case INIT:
                    if (isDoubleQuotationMark(currentChar)) {
                        currentStatus = Status.READ_STRING;
                    } else {
                        break;
                    }
                    break;
                case READ_STRING:
                    if (isDoubleQuotationMark(currentChar)) {
                        currentStatus = Status.END;
                    } else if (isReverseSolidus(currentChar)) {
                        currentStatus = Status.ESCAPE_CHARACTER;
                    } else if (isControl(currentChar)) {
                        break;
                    }
                    break;
                case ESCAPE_CHARACTER:
                    if (isDoubleQuotationMark(currentChar) ||
                            isReverseSolidus(currentChar) ||
                            isSolidus(currentChar) ||
                            isBackSpace(currentChar) ||
                            isFormfeed(currentChar) ||
                            isLineFeed(currentChar) ||
                            isCarriageReturn(currentChar) ||
                            isHorizontalTab(currentChar)) {
                        currentStatus = Status.READ_STRING;
                    } else if (isUnicode(currentChar)) {
                        currentStatus = Status.UNICODE_0;
                    } else {
                        break;
                    }
                    break;
                case UNICODE_0:
                    if (isHexDigit(currentChar)) {
                        currentStatus = Status.UNICODE_1;
                    } else {
                        break;
                    }
                    break;
                case UNICODE_1:
                    if (isHexDigit(currentChar)) {
                        currentStatus = Status.UNICODE_2;
                    } else {
                        break;
                    }
                    break;
                case UNICODE_2:
                    if (isHexDigit(currentChar)) {
                        currentStatus = Status.UNICODE_3;
                    } else {
                        break;
                    }
                    break;
                case UNICODE_3:
                    if (isHexDigit(currentChar)) {
                        currentStatus = Status.READ_STRING;
                    } else {
                        break;
                    }
                    break;
                case END:
                default:
                    throw new ParseException();
            }
            ++i;
        }
        if (currentStatus != Status.END) {
            throw new ParseException();
        }
        return i;
    }
}
