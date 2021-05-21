package me.yz.yzjson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonObjectParser {
    private enum Status {
        INIT,
        WHITESPACE_OR_KEY_OR_END,
        WHITESPACE_OR_KEY,
        WHITESPACE_OR_COLON,
        WHITESPACE_OR_COMMA_OR_END,
        WHITESPACE_OR_VALUE,
        END
    }

    public static int parseObject(final CharSequence sequence, final int index, final Map<String, Object> jsonObject) {
        char c;
        String lastKey = null;

        int i = index;
        Status currentStatus = Status.INIT;

        out:
        while (i != sequence.length()) {
            c = sequence.charAt(i);
            switch (currentStatus) {
                case INIT:
                    if (isLeftBrace(c)) {
                        i += 1;
                        currentStatus = Status.WHITESPACE_OR_KEY_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_OR_KEY_OR_END:
                    if (isQuotationMark(c)) {
                        final ImmutableTypeHolder<String> strHolder = new ImmutableTypeHolder<>();
                        i = StringParser.parse(sequence, i, strHolder);
                        lastKey = strHolder.getValue();
                        currentStatus = Status.WHITESPACE_OR_COLON;
                    } else if (isRightBrace(c)) {
                        i += 1;
                        currentStatus = Status.END;
                    } else if (isWhitespace(c)) {
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_OR_KEY:
                    if (isQuotationMark(c)) {
                        final ImmutableTypeHolder<String> strHolder = new ImmutableTypeHolder<>();
                        i = StringParser.parse(sequence, i, strHolder);
                        lastKey = strHolder.getValue();
                        currentStatus = Status.WHITESPACE_OR_COLON;
                    } else if (isWhitespace(c)) {
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_OR_COLON:
                    if (isColon(c)) {
                        i += 1;
                        currentStatus = Status.WHITESPACE_OR_VALUE;
                    } else if (isWhitespace(c)) {
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_OR_VALUE:
                    if (isQuotationMark(c)) {
                        final ImmutableTypeHolder<String> strHolder = new ImmutableTypeHolder<>();
                        i = StringParser.parse(sequence, i, strHolder);
                        jsonObject.put(lastKey, strHolder.getValue());
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isNumberValue(c)) {
                        final ImmutableTypeHolder<BigDecimal> numberHolder = new ImmutableTypeHolder<>();
                        i = NumberValueParser.parse(sequence, i, numberHolder);
                        jsonObject.put(lastKey, numberHolder.getValue());
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isLeftBrace(c)) {
                        final Map<String, Object> subJsonObject = new HashMap<>();
                        i = parseObject(sequence, i, subJsonObject);
                        jsonObject.put(lastKey, subJsonObject);
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isLeftBracket(c)) {
                        final List<Object> subJsonArray = new ArrayList<>();
                        i = JsonArrayParser.parseArray(sequence, i, subJsonArray);
                        jsonObject.put(lastKey, subJsonArray);
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isTureValue(c)) {
                        i = JsonConstantParser.parseTrue(sequence, i);
                        jsonObject.put(lastKey, true);
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isFalseValue(c)) {
                        i = JsonConstantParser.parseFalse(sequence, i);
                        jsonObject.put(lastKey, false);
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isNullValue(c)) {
                        i = JsonConstantParser.parseNull(sequence, i);
                        jsonObject.put(lastKey, null);
                        currentStatus = Status.WHITESPACE_OR_COMMA_OR_END;
                    } else if (isWhitespace(c)) {
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_OR_COMMA_OR_END:
                    if (isComma(c)) {
                        i += 1;
                        currentStatus = Status.WHITESPACE_OR_KEY;
                    } else if (isRightBrace(c)) {
                        i += 1;
                        currentStatus = Status.END;
                    } else if (isWhitespace(c)) {
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case END:
                    break out;
            }
        }

        if (currentStatus != Status.END) {
            throw new ParseException();
        }

        return i;
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
        return c == 'f';
    }

    private static boolean isNullValue(final char c) {
        return c == 'n';
    }

    private static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }
}
