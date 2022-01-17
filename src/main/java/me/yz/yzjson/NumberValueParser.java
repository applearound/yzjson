package me.yz.yzjson;

import java.math.BigDecimal;

public class NumberValueParser {
    private enum Status {
        INIT,
        ZERO_OR_POSITIVE_DIGIT,
        DIGIT_FRACTION_OR_EXPONENT_OR_END,
        FRACTION_OR_EXPONENT_OR_END,
        FRACTION_NUMBER,
        FRACTION_NUMBER_OR_EXPONENT_OR_END,
        MARK_OR_EXPONENT_NUMBER,
        EXPONENT_NUMBER,
        EXPONENT_NUMBER_OR_END
    }

    public static int parse(final CharSequence sequence, final int index, final ImmutableTypeHolder<BigDecimal> numberHolder) {
        int i = index;
        char c;
        Status currentStatus = Status.INIT;
        out:
        while (i != sequence.length()) {
            c = sequence.charAt(i);
            switch (currentStatus) {
                case INIT:
                    if (isNegativeMark(c)) {
                        currentStatus = Status.ZERO_OR_POSITIVE_DIGIT;
                    } else if (isZero(c)) {
                        currentStatus = Status.FRACTION_OR_EXPONENT_OR_END;
                    } else if (isPositiveDigit(c)) {
                        currentStatus = Status.DIGIT_FRACTION_OR_EXPONENT_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case ZERO_OR_POSITIVE_DIGIT:
                    if (isZero(c)) {
                        currentStatus = Status.FRACTION_OR_EXPONENT_OR_END;
                    } else if (isPositiveDigit(c)) {
                        currentStatus = Status.DIGIT_FRACTION_OR_EXPONENT_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case FRACTION_OR_EXPONENT_OR_END:
                    if (isDot(c)) {
                        currentStatus = Status.FRACTION_NUMBER;
                    } else if (isExponent(c)) {
                        currentStatus = Status.MARK_OR_EXPONENT_NUMBER;
                    } else {
                        break out;
                    }
                    break;
                case DIGIT_FRACTION_OR_EXPONENT_OR_END:
                    if (isDot(c)) {
                        currentStatus = Status.FRACTION_NUMBER;
                    } else if (isExponent(c)) {
                        currentStatus = Status.MARK_OR_EXPONENT_NUMBER;
                    } else if (!isDigit(c)) {
                        break out;
                    }
                    break;
                case FRACTION_NUMBER:
                    if (isDigit(c)) {
                        currentStatus = Status.FRACTION_NUMBER_OR_EXPONENT_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case FRACTION_NUMBER_OR_EXPONENT_OR_END:
                    if (isExponent(c)) {
                        currentStatus = Status.MARK_OR_EXPONENT_NUMBER;
                    } else if (!isDigit(c)) {
                        break out;
                    }
                    break;
                case MARK_OR_EXPONENT_NUMBER:
                    if (isMark(c)) {
                        currentStatus = Status.EXPONENT_NUMBER;
                    } else if (isDigit(c)) {
                        currentStatus = Status.EXPONENT_NUMBER_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case EXPONENT_NUMBER:
                    if (isDigit(c)) {
                        currentStatus = Status.EXPONENT_NUMBER_OR_END;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case EXPONENT_NUMBER_OR_END:
                    if (!isDigit(c)) {
                        break out;
                    }
                    break;
            }
            i += 1;
        }

        if (currentStatus != Status.FRACTION_OR_EXPONENT_OR_END &&
                currentStatus != Status.DIGIT_FRACTION_OR_EXPONENT_OR_END &&
                currentStatus != Status.FRACTION_NUMBER_OR_EXPONENT_OR_END &&
                currentStatus != Status.EXPONENT_NUMBER_OR_END) {
            throw new ParseException();
        }

        if (numberHolder != null) {
            numberHolder.setValue(new BigDecimal(sequence.subSequence(index, i).toString()));
        }

        return i;
    }

    private static boolean isNegativeMark(final char c) {
        return c == '-';
    }

    private static boolean isPositiveMark(final char c) {
        return c == '+';
    }

    private static boolean isMark(final char c) {
        return isNegativeMark(c) || isPositiveMark(c);
    }

    private static boolean isPositiveDigit(final char c) {
        return '1' <= c && c <= '9';
    }

    private static boolean isZero(final char c) {
        return c == '0';
    }

    private static boolean isDigit(final char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isDot(final char c) {
        return c == '.';
    }

    private static boolean isExponent(final char c) {
        return c == 'E' || c == 'e';
    }
}
