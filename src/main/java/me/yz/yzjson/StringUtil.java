package me.yz.yzjson;

public class StringUtil {
    private final String key;

    public StringUtil(String key) {
        this.key = key;
    }

    private static boolean isDoubleQuoteMark(final char c) {
        return c == '"';
    }

    private static boolean isBackSlash(final char c) {
        return c == '\\';
    }

    public static StringUtil read(final CharSequence sequence) {
        int currentStatus = 0;
        int index = 0;
        char currentChar;
        while (index != sequence.length()) {
            currentChar = sequence.charAt(index++);
            switch (currentStatus) {
                case 0:
                    if (isDoubleQuoteMark(currentChar)) {
                        currentStatus = 1;
                    } else {
                        throw new ParseException();
                    }
                    break;
                case 1:
                    if (isDoubleQuoteMark(currentChar)) {
                        currentStatus = 2;
                    } else if (isBackSlash(currentChar)) {
                        currentStatus = 3;
                    } else {
                        currentStatus = 4;
                    }
                    break;
                case 2:
                    break;
                case 3:

                    break;
                case 4:
                    if (isDoubleQuoteMark(currentChar)) {
                        currentStatus = 2;
                    } else if (isBackSlash(currentChar)) {
                        currentStatus = 3;
                    } else {
                        currentStatus = 4;
                    }
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 10:
                    break;
                case 11:
                    break;
                case 12:
                    break;
                case 13:
                    break;
                case 14:
                    break;
            }
        }
    }
}
