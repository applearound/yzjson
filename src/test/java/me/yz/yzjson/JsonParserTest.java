package me.yz.yzjson;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class JsonParserTest {
    private String sampleJsonString = "{\"widget\": {\n" +
            "    \"debug\": \"on\",\n" +
            "    \"window\": {\n" +
            "        \"title\": \"Sample Konfabulator Widget\",\n" +
            "        \"name\": \"main_window\",\n" +
            "        \"width\": 500,\n" +
            "        \"height\": 500\n" +
            "    },\n" +
            "    \"image\": { \n" +
            "        \"src\": \"Images/Sun.png\",\n" +
            "        \"name\": \"sun1\",\n" +
            "        \"hOffset\": 250,\n" +
            "        \"vOffset\": 250,\n" +
            "        \"alignment\": \"center\"\n" +
            "    },\n" +
            "    \"text\": {\n" +
            "        \"data\": \"Click Here\",\n" +
            "        \"size\": 36,\n" +
            "        \"style\": \"bold\",\n" +
            "        \"name\": \"text1\",\n" +
            "        \"hOffset\": 250,\n" +
            "        \"vOffset\": 100,\n" +
            "        \"alignment\": \"center\",\n" +
            "        \"onMouseUp\": \"sun1.opacity = (sun1.opacity / 100) * 90;\"\n" +
            "    }\n" +
            "}}";


    @Test
    void testFastJson() {
        for (int i = 0; i < 10000; i++) {
            final Map<String, Object> map = JSON.parseObject(sampleJsonString, Map.class);
            System.out.println(map);
        }
    }

    @Test
    void testYzJson() {
        for (int i = 0; i < 10000; i++) {
            final Map<String, Object> map = new HashMap<>();
            JsonObjectParser.parseObject(sampleJsonString, 0, map);
            System.out.println(map);
        }
    }
}
