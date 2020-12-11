package me.yz.yzjson;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {

    private enum Status {
        INIT,
        KEY_OR_EMPTY,
        KEY,
        COLON,
        COMMA_OR_END,
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

    public static int parseObject(final CharSequence sequence, final int index, final Map<String, Object> map) {
        Status currentStatus = Status.INIT;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case INIT:
                    if (isLeftBrace(c)) {
                        currentStatus = Status.KEY_OR_EMPTY;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case KEY_OR_EMPTY:
                    if (isQuotationMark(c)) {
                        final StringParser.PartialResult parse = StringParser.parse(sequence, i - 1);
                        i = parse.getIndex();
                        System.out.println("Key: " + parse.getResult());
                        currentStatus = Status.COLON;
                    } else if (isRightBrace(c)) {
                        currentStatus = Status.END;
                    } else if (!isWhitespace(c)) {
                        throw new ParseException();
                    }
                    break;
                case KEY:
                    if (isQuotationMark(c)) {
                        final StringParser.PartialResult parse = StringParser.parse(sequence, i - 1);
                        i = parse.getIndex();
                        System.out.println("Key: " + parse.getResult());
                        currentStatus = Status.COLON;
                    } else if (!isWhitespace(c)) {
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
                        final StringParser.PartialResult parse = StringParser.parse(sequence, i - 1);
                        i = parse.getIndex();
                        System.out.println("Value: " + parse.getResult());
                        currentStatus = Status.COMMA_OR_END;
                    } else if (isNumberValue(c)) {
                        i = NumberValueParser.parse(sequence, i - 1);
                        currentStatus = Status.COMMA_OR_END;
                    } else if (isLeftBrace(c)) {
                        i = parseObject(sequence, i - 1, new HashMap<>());
                        currentStatus = Status.COMMA_OR_END;
                    } else if (isLeftBracket(c)) {
                        i = parseArray(sequence, i - 1);
                        currentStatus = Status.COMMA_OR_END;
                    } else if (isTureValue(c)) {
                        i = parseTrue(sequence, i - 1);
                        currentStatus = Status.COMMA_OR_END;
                    } else if (isFalseValue(c)) {
                        i = parseFalse(sequence, i - 1);
                        currentStatus = Status.COMMA_OR_END;
                    } else if (isNullValue(c)) {
                        i = parseNull(sequence, i - 1);
                        currentStatus = Status.COMMA_OR_END;
                    } else if (!isWhitespace(c)) {
                        throw new ParseException();
                    }
                    break;
                case COMMA_OR_END:
                    if (isComma(c)) {
                        currentStatus = Status.KEY;
                    } else if (isRightBrace(c)) {
                        currentStatus = Status.END;
                    } else if (!isWhitespace(c)) {
                        throw new ParseException();
                    }
                    break;
                case END:
                default:
                    throw new ParseException();
            }
        }

        return i;
    }

    public static int parseArray(final CharSequence sequence, final int index) {
        return 1;
    }

    public static int parseTrue(final CharSequence sequence, final int index) {
        int currentStatus = 0;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case 0:
                    if (c == 't') {
                        currentStatus = 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 1:
                    if (c == 'r') {
                        currentStatus = 2;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 2:
                    if (c == 'u') {
                        currentStatus = 3;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 3:
                    if (c == 'e') {
                        currentStatus = 4;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 4:
                    return i - 1;
                default:
                    throw new ParseException();
            }
        }
        return i;
    }

    public static int parseFalse(final CharSequence sequence, final int index) {
        int currentStatus = 0;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case 0:
                    if (c == 'f') {
                        currentStatus = 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 1:
                    if (c == 'a') {
                        currentStatus = 2;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 2:
                    if (c == 'l') {
                        currentStatus = 3;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 3:
                    if (c == 's') {
                        currentStatus = 4;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 4:
                    if (c == 'e') {
                        currentStatus = 5;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 5:
                    return i - 1;
                default:
                    throw new ParseException();
            }
        }
        return i;
    }

    public static int parseNull(final CharSequence sequence, final int index) {
        int currentStatus = 0;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case 0:
                    if (c == 'n') {
                        currentStatus = 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 1:
                    if (c == 'u') {
                        currentStatus = 2;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 2:
                    if (c == 'l') {
                        currentStatus = 3;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 3:
                    if (c == 'l') {
                        currentStatus = 4;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 4:
                    return i - 1;
                default:
                    throw new ParseException();
            }
        }
        return i;
    }
}
