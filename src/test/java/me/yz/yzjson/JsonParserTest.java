package me.yz.yzjson;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void parseObject() {
        String testString = "{\"key\": \"value\"   ,  \"key2\": -1.234e10 }";
        final Map<String, Object> map = new HashMap<>();
        int i = JsonParser.parseObject(testString, 0, map);
        assertEquals(i, testString.length());
    }

    @Test
    void parseArray() {
    }

    @Test
    void parseStringValue() {
    }

    @Test
    void parseTrueValue() {
        String testString = "   \t \t    \r\n true   \r\r\t";

        int i = JsonParser.parseTrue(testString, 0);
        assertEquals(testString.length(), i);

        testString = "true";
        i = JsonParser.parseTrue(testString, 0);
        assertEquals(testString.length(), i);
    }

    @Test
    void parseFalseValue() {
        String testString = "   \t \t    \r\n false   \r\r\t";

        int i = JsonParser.parseFalse(testString, 0);
        assertEquals(testString.length(), i);

        testString = "false";
        i = JsonParser.parseFalse(testString, 0);
        assertEquals(testString.length(), i);
    }

    @Test
    void parseNullValue() {
        String testString = "   \t \t    \r\n null   \r\r\t";

        int i = JsonParser.parseNull(testString, 0);
        assertEquals(testString.length(), i);

        testString = "null";
        i = JsonParser.parseNull(testString, 0);
        assertEquals(testString.length(), i);
    }
}
