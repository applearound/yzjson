package me.yz.yzjson;

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
        INIT_DOUBLE_QUOTATION,
        READ_STRING_OR_END,
        ESCAPE,
        UNICODE,
        END_DOUBLE_QUOTATION
    }

    public static int parse(final CharSequence sequence, final int index, final ImmutableTypeHolder<String> strHolder) {
        final StringBuilder sb = new StringBuilder();
        int i = index;
        char c;
        Status currentStatus = Status.INIT_DOUBLE_QUOTATION;
        out:
        while (i != sequence.length()) {
            c = sequence.charAt(i);
            switch (currentStatus) {
                case INIT_DOUBLE_QUOTATION:
                    if (isDoubleQuotationMark(c)) {
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case READ_STRING_OR_END:
                    if (isDoubleQuotationMark(c)) {
                        i += 1;
                        currentStatus = Status.END_DOUBLE_QUOTATION;
                    } else if (isReverseSolidus(c)) {
                        i += 1;
                        currentStatus = Status.ESCAPE;
                    } else if (!isControl(c)) {
                        sb.append(c);
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case ESCAPE:
                    if (isDoubleQuotationMark(c)) {
                        sb.append('"');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isReverseSolidus(c)) {
                        sb.append('\\');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isSolidus(c)) {
                        sb.append('/');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isBackSpace(c)) {
                        sb.append('\b');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isFormfeed(c)) {
                        sb.append('\f');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isLineFeed(c)) {
                        sb.append('\n');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isCarriageReturn(c)) {
                        sb.append('\r');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isHorizontalTab(c)) {
                        sb.append('\t');
                        i += 1;
                        currentStatus = Status.READ_STRING_OR_END;
                    } else if (isUnicode(c)) {
                        i += 1;
                        currentStatus = Status.UNICODE;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case UNICODE:
                    if (i + 3 >= sequence.length()) {
                        throw new ParseException();
                    }

                    final char uni0 = c;
                    final char uni1 = sequence.charAt(i + 1);
                    final char uni2 = sequence.charAt(i + 2);
                    final char uni3 = sequence.charAt(i + 3);

                    if (isHexDigit(uni0) && isHexDigit(uni1) && isHexDigit(uni2) && isHexDigit(uni3)) {
                        sb.append((char) (
                                hexToDecimalInt(uni3) +
                                        (hexToDecimalInt(uni2) << 4) +
                                        (hexToDecimalInt(uni1) << 8) +
                                        (hexToDecimalInt(uni0) << 12)
                        ));
                        currentStatus = Status.READ_STRING_OR_END;
                        i += 4;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case END_DOUBLE_QUOTATION:
                    break out;
            }
        }
        if (currentStatus != Status.END_DOUBLE_QUOTATION) {
            throw new ParseException();
        }
        if (strHolder != null) {
            strHolder.setValue(sb.toString());
        }
        return i;
    }
}
