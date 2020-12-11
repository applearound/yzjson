package me.yz.yzjson;

import org.junit.jupiter.api.Test;

class NumberValueParserTest {

    @Test
    void parse() {
//        NumberValueParser.parse("  \r\r\n\t  -01.50e00123   \r \n\t  ", 0);
        String bear = "🐻";
        System.out.println(bear.codePointAt(0));
        System.out.println(bear.codePointAt(1));
        System.out.println(bear.codePointCount(0, 2));
    }
}
