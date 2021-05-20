package me.yz.yzjson;

import java.io.OutputStream;

public class StringParser {
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

    private enum Status {
        INIT,
        READ_STRING_OR_END,
        ESCAPE,
        UNI0,
        UNI1,
        UNI2,
        UNI3,
        END
    }

    public static int parse(final CharSequence sequence, final int index) {
        final StringBuilder sb = new StringBuilder();
        int i = index;
        char c;
        Status currentStatus = Status.INIT;
        while (i != sequence.length() && currentStatus != Status.END) {
            c = sequence.charAt(i);
            switch (currentStatus) {
                case INIT:
                    if (isDoubleQuotationMark(c)) {
                        currentStatus = Status.READ_STRING_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case READ_STRING_OR_END:
                    if (isDoubleQuotationMark(c)) {
                        currentStatus = Status.END;
                    } else if (isReverseSolidus(c)) {
                        currentStatus = Status.ESCAPE;
                    } else if (!isControl(c)) {
                        sb.append(c);
                        currentStatus = Status.READ_STRING_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case ESCAPE:
                    if (isDoubleQuotationMark(c)) {
                        sb.append("\"");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isReverseSolidus(c)) {
                        sb.append("\\");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isSolidus(c)) {
                        sb.append("/");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isBackSpace(c)) {
                        sb.append("\b");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isFormfeed(c)) {
                        sb.append("\f");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isLineFeed(c)) {
                        sb.append("\n");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isCarriageReturn(c)) {
                        sb.append("\r");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isHorizontalTab(c)) {
                        sb.append("\t");
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isUnicode(c)) {
                        currentStatus = Status.UNI0;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case UNI0:
                    if (isHexDigit(c)) {
                        currentStatus = Status.UNI1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case UNI1:
                    if (isHexDigit(c)) {
                        currentStatus = Status.UNI2;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case UNI2:
                    if (isHexDigit(c)) {
                        currentStatus = Status.UNI3;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case UNI3:
                    if (isHexDigit(c)) {
                        currentStatus = Status.READ_STRING_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case END:
                    break;
            }
            i += 1;
        }
        if (currentStatus != Status.END) {
            throw new ParseException();
        }
        System.out.println(sb);
        return i;
    }

    public static void main(String[] args) {
        int i = StringParser.parse("\"\\ta123\"123", 0);
        System.out.println(i);
    }
}
