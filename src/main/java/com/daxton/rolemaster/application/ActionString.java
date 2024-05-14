package com.daxton.rolemaster.application;

import java.util.HashMap;
import java.util.Map;

public class ActionString {

    public static Map<String, String> convertToMap(String input) {
        // 去掉首尾的标识符
        String keyValueString = input.substring(input.indexOf('[') + 1, input.indexOf(']'));

        // 按分号分割键值对
        String[] keyValuePairs = keyValueString.split("; ");

        // 创建 Map
        Map<String, String> map = new HashMap<>();

        // 遍历键值对数组，再按等号分割每个键值对，并放入 Map
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            map.put(key, value);
        }

        return map;
    }

}
