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
        EXPONENT_NUMBER_OR_END,
        END
    }

    private static boolean isNegativeMark(final char c) {
        return c == '-';
    }

    private static boolean isPositiveMark(final char c) {
        return c == '+';
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

    public static int parse(final CharSequence sequence, final int index) {
        int i = index;
        char c;
        Status currentStatus = Status.INIT;
        while (i != sequence.length() && currentStatus != Status.END) {
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
                        currentStatus = Status.END;
                    }
                    break;
                case DIGIT_FRACTION_OR_EXPONENT_OR_END:
                    if (isDot(c)) {
                        currentStatus = Status.FRACTION_NUMBER;
                    } else if (isExponent(c)) {
                        currentStatus = Status.MARK_OR_EXPONENT_NUMBER;
                    } else if (isDigit(c)) {
                        currentStatus = Status.DIGIT_FRACTION_OR_EXPONENT_OR_END;
                    } else {
                        currentStatus = Status.END;
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
                    } else if (isDigit(c)) {
                        currentStatus = Status.FRACTION_NUMBER_OR_EXPONENT_OR_END;
                    } else {
                        currentStatus = Status.END;
                    }
                    break;
                case MARK_OR_EXPONENT_NUMBER:
                    if (isPositiveMark(c) || isNegativeMark(c)) {
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
                    if (isDigit(c)) {
                        currentStatus = Status.EXPONENT_NUMBER_OR_END;
                    } else {
                        currentStatus = Status.END;
                    }
                    break;
                case END:
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
        System.out.println(new BigDecimal(sequence.subSequence(index, i).toString()));
        return i;
    }

    public static void main(String[] args) {
        String a = "23e4";
        int i = NumberValueParser.parse(a, 0);
        System.out.println(i);
    }
}
