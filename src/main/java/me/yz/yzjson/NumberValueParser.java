package me.yz.yzjson;

public class NumberValueParser {

    private enum Status {
        INIT,
        ZERO_OR_POSITIVE,
        INTEGER,
        ZERO,
        FRACTION_NUMBER,
        FRACTION_CONTINUE,
        EXPONENT,
        EXPONENT_NUMBER,
        EXPONENT_NUMBER_CONTINUE;
    }

    private static boolean isNegative(final char c) {
        return c == '-';
    }

    private static boolean isPositive(final char c) {
        return c == '+';
    }

    private static boolean isPositiveInteger(final char c) {
        return '1' <= c && c <= '9';
    }

    private static boolean isZero(final char c) {
        return c == '0';
    }

    private static boolean isInteger(final char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isDot(final char c) {
        return c == '.';
    }

    private static boolean isExponent(final char c) {
        return c == 'E' || c == 'e';
    }

    public static int parse(final CharSequence sequence, final int index) {
        Status currentStatus = Status.INIT;
        int i = index;
        char c;
        while (i != sequence.length()) {
            c = sequence.charAt(i++);
            switch (currentStatus) {
                case INIT:
                    if (isNegative(c)) {
                        currentStatus = Status.ZERO_OR_POSITIVE;
                    } else if (isZero(c)) {
                        currentStatus = Status.ZERO;
                    } else if (isPositiveInteger(c)) {
                        currentStatus = Status.INTEGER;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case ZERO_OR_POSITIVE:
                    if (isZero(c)) {
                        currentStatus = Status.ZERO;
                    } else if (isPositiveInteger(c)) {
                        currentStatus = Status.INTEGER;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case ZERO:
                    if (isDot(c)) {
                        currentStatus = Status.FRACTION_NUMBER;
                    } else if (isExponent(c)) {
                        currentStatus = Status.EXPONENT;
                    } else {
                        return i - 1;
                    }
                    break;
                case INTEGER:
                    if (isDot(c)) {
                        currentStatus = Status.FRACTION_NUMBER;
                    } else if (isExponent(c)) {
                        currentStatus = Status.EXPONENT;
                    } else if (!isInteger(c)) {
                        return i - 1;
                    }
                    break;
                case FRACTION_NUMBER:
                    if (isInteger(c)) {
                        currentStatus = Status.FRACTION_CONTINUE;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case FRACTION_CONTINUE:
                    if (isExponent(c)) {
                        currentStatus = Status.EXPONENT;
                    } else if (!isInteger(c)) {
                        return i - 1;
                    }
                    break;
                case EXPONENT:
                    if (isPositive(c) || isNegative(c)) {
                        currentStatus = Status.EXPONENT_NUMBER;
                    } else if (isInteger(c)) {
                        currentStatus = Status.EXPONENT_NUMBER_CONTINUE;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case EXPONENT_NUMBER:
                    if (isInteger(c)) {
                        currentStatus = Status.EXPONENT_NUMBER_CONTINUE;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case EXPONENT_NUMBER_CONTINUE:
                    if (!isInteger(c)) {
                        return i - 1;
                    }
                    break;
                default:
                    throw new ParseException();
            }
        }
        if (currentStatus != Status.ZERO &&
                currentStatus != Status.INTEGER &&
                currentStatus != Status.FRACTION_CONTINUE &&
                currentStatus != Status.EXPONENT_NUMBER_CONTINUE) {
            throw new ParseException();
        }
        return i;
    }
}
