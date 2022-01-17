package me.yz.yzjson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonArrayParser {
    private enum Status {
        INIT,
        WHITESPACE_OR_VALUE_OR_END,
        WHITESPACE_COMMA_OR_END,
        END
    }

    public static int parseArray(final CharSequence sequence, final int index, final List<Object> jsonArray) {
        int i = index;
        char c;
        Status currentStatus = Status.INIT;
        out:
        while (i != sequence.length()) {
            c = sequence.charAt(i);
            switch (currentStatus) {
                case INIT:
                    if (isLeftBracket(c)) {
                        i += 1;
                        currentStatus = Status.WHITESPACE_OR_VALUE_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_OR_VALUE_OR_END:
                    if (isQuotationMark(c)) {
                        final ImmutableTypeHolder<String> strHolder = new ImmutableTypeHolder<>();
                        i = StringParser.parse(sequence, i, strHolder);
                        jsonArray.add(strHolder.getValue());
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isNumberValue(c)) {
                        final ImmutableTypeHolder<BigDecimal> numberHolder = new ImmutableTypeHolder<>();
                        i = NumberValueParser.parse(sequence, i, numberHolder);
                        jsonArray.add(numberHolder.getValue());
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isLeftBrace(c)) {
                        final Map<String, Object> subJsonObject = new HashMap<>();
                        i = JsonObjectParser.parseObject(sequence, i, subJsonObject);
                        jsonArray.add(subJsonObject);
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isLeftBracket(c)) {
                        final List<Object> subJsonArray = new ArrayList<>();
                        i = parseArray(sequence, i, subJsonArray);
                        jsonArray.add(subJsonArray);
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isTureValue(c)) {
                        i = JsonConstantParser.parseTrue(sequence, i);
                        jsonArray.add(true);
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isFalseValue(c)) {
                        i = JsonConstantParser.parseFalse(sequence, i);
                        jsonArray.add(false);
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isNullValue(c)) {
                        i = JsonConstantParser.parseNull(sequence, i);
                        jsonArray.add(null);
                        currentStatus = Status.WHITESPACE_COMMA_OR_END;
                    } else if (isRightBracket(c)) {
                        i += 1;
                        currentStatus = Status.END;
                    } else if (isWhitespace(c)) {
                        i += 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case WHITESPACE_COMMA_OR_END:
                    if (isComma(c)) {
                        i += 1;
                        currentStatus = Status.WHITESPACE_OR_VALUE_OR_END;
                    } else if (isRightBracket(c)) {
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

    private static boolean isLeftBracket(final char c) {
        return c == '[';
    }

    private static boolean isRightBracket(final char c) {
        return c == ']';
    }

    private static boolean isQuotationMark(final char c) {
        return c == '"';
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
