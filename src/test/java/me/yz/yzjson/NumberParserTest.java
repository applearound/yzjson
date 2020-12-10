package me.yz.yzjson;

import org.junit.jupiter.api.Test;

class NumberParserTest {

    @Test
    void read() {
        NumberParser.parse("-01.50e00123", 0);
    }
}