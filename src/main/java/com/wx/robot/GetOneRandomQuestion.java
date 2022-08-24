package com.wx.robot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author wangxiang
 * @date 2022/8/12 10:50
 * @description get a leetcode's question at random
 */
public class GetOneRandomQuestion {
    static Map<String, String> map = new HashMap<>();

    public static void main(String[] args) {
        InputStream is = GetOneRandomQuestion.class.getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Enumeration mappingPropertiesEnumeration = properties.propertyNames();
        while (mappingPropertiesEnumeration.hasMoreElements()) {
            String key = (String) mappingPropertiesEnumeration.nextElement();
            String value = properties.getProperty(key);
            map.put(key, value);
        }

        map.forEach((k, v) -> System.out.println(k + ":" + v));
    }


    private void getRandomOne() {

    }
}
