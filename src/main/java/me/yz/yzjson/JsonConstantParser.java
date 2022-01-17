package me.yz.yzjson;

public class JsonConstantParser {
    public static int parseTrue(final CharSequence sequence, final int index) {
        if (index + 3 >= sequence.length()) {
            throw new ParseException();
        }

        final char t = sequence.charAt(index);
        final char r = sequence.charAt(index + 1);
        final char u = sequence.charAt(index + 2);
        final char e = sequence.charAt(index + 3);

        if (t == 't' && r == 'r' && u == 'u' && e == 'e') {
            return index + 4;
        } else {
            throw new ParseException();
        }
    }

    public static int parseFalse(final CharSequence sequence, final int index) {
        if (index + 4 >= sequence.length()) {
            throw new ParseException();
        }

        final char f = sequence.charAt(index);
        final char a = sequence.charAt(index + 1);
        final char l = sequence.charAt(index + 2);
        final char s = sequence.charAt(index + 3);
        final char e = sequence.charAt(index + 4);

        if (f == 'f' && a == 'a' && l == 'l' && s == 's' && e == 'e') {
            return index + 5;
        } else {
            throw new ParseException();
        }
    }

    public static int parseNull(final CharSequence sequence, final int index) {
        if (index + 3 >= sequence.length()) {
            throw new ParseException();
        }

        final char n = sequence.charAt(index);
        final char u = sequence.charAt(index + 1);
        final char l1 = sequence.charAt(index + 2);
        final char l2 = sequence.charAt(index + 3);

        if (n == 'n' && u == 'u' && l1 == 'l' && l2 == 'l') {
            return index + 4;
        } else {
            throw new ParseException();
        }
    }
}
